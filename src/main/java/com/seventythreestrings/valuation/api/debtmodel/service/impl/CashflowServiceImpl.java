package com.seventythreestrings.valuation.api.debtmodel.service.impl;

import com.seventythreestrings.valuation.api.debtmodel.dto.*;
import com.seventythreestrings.valuation.api.debtmodel.model.*;
import com.seventythreestrings.valuation.api.debtmodel.repository.CashflowRepository;
import com.seventythreestrings.valuation.api.debtmodel.service.CashflowService;
import com.seventythreestrings.valuation.api.debtmodel.service.DebtModelInputService;
import com.seventythreestrings.valuation.api.debtmodel.service.DebtModelService;
import com.seventythreestrings.valuation.api.debtmodel.util.CashflowUtil;
import com.seventythreestrings.valuation.api.exception.AppException;
import com.seventythreestrings.valuation.api.exception.ErrorCodesAndMessages;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CashflowServiceImpl implements CashflowService {
    private final CashflowRepository repository;
    private final DebtModelService debtModelService;
    private final DebtModelInputService debtModelInputService;
    private final ModelMapper modelMapper;

    @SneakyThrows
    @Override
    public Cashflow getCashflowForDebtModel(Long debtModelId) {
        return repository
                .findFirstByDebtModelId(debtModelId)
                .orElseThrow(() -> new AppException(ErrorCodesAndMessages.NOT_FOUND_EXCEPTION));
    }

    @Override
    public Cashflow generateCashflowForDebtModel(Long debtModelId) {
        DebtModel debtModel = debtModelService.get(debtModelId);
        List<DebtModelInputDto> inputs = debtModelInputService.getInputsForDebtModel(debtModelId);

        Cashflow cashflow = new Cashflow();
        addGeneralDetailsToCashflow(cashflow, inputs);
        addCashflowSchedulesToCashflow(cashflow, inputs);
        addCashflowSummaryToCashflow(cashflow, inputs);
        cashflow.setDebtModel(debtModel);

        // update Analysis date in Debt Model
        debtModel.setAnalysisDate(LocalDateTime.now());
        debtModelService.save(debtModel);

        return saveCashflow(cashflow);
    }

    @Override
    public void deleteCashflowForDebtModel(Long debtModelId) {
        Cashflow cashflow = getCashflowForDebtModel(debtModelId);
        DebtModel debtModel = cashflow.getDebtModel();
        repository.delete(cashflow);

        // update Analysis date in Debt Model
        debtModel.setAnalysisDate(null);
        debtModelService.save(debtModel);
    }

    public Cashflow saveCashflow(Cashflow cashflow) {
        return repository.save(cashflow);
    }

    private void addGeneralDetailsToCashflow(Cashflow cashflow, List<DebtModelInputDto> inputs) {
        Optional<GeneralDetails> generalDetails = getGeneralDetailsFromInputs(inputs);
        if (generalDetails.isPresent()) {
            cashflow.setOriginationDate(generalDetails.get().getOriginationDate());
            cashflow.setMaturityDate(generalDetails.get().getMaturityDate());
            cashflow.setValuationDate(generalDetails.get().getValuationDate());
            cashflow.setDiscountRate(generalDetails.get().getDiscountRate());
            cashflow.setDayCountConvention(generalDetails.get().getDayCountConvention());
        }
    }

    private void addCashflowSchedulesToCashflow(Cashflow cashflow, List<DebtModelInputDto> inputs) {
        Set<CashflowSchedule> schedules = new LinkedHashSet<>();
        Set<CashflowScheduleDate> scheduleDates = getCashflowScheduleDates(inputs);
        Optional<GeneralDetails> generalDetailsInput = getGeneralDetailsFromInputs(inputs);
        Optional<InterestDetails> interestDetailsInput = getInterestDetailsFromInputs(inputs);
        Optional<PrepaymentDetails> prepaymentDetailsInput = getPrepaymentDetailsFromInputs(inputs);
        if (!generalDetailsInput.isPresent()) {
            return;
        }
        boolean isInterestAccrued = interestDetailsInput.map(interestDetails -> interestDetails.getInterestPaidOrAccrued().equals(InterestType.ACCRUED)).orElse(false);
        double principalAmount = generalDetailsInput.get().getPrincipalAmount();

        DayCountConvention dayCountConvention = cashflow.getDayCountConvention();
        if (dayCountConvention == null) {
            dayCountConvention = DayCountConvention.ACTUAL_BY_ACTUAL;
        }


        CashflowSchedule previousCashflowSchedule = null;
        double outstandingInterestOutflow = 0;
        for (CashflowScheduleDate scheduleDate: scheduleDates) {
            double principalRepayment = 0;
            double cashMovement = 0;
            double principalOutstanding = 0;
            if (previousCashflowSchedule != null) {
                principalOutstanding = previousCashflowSchedule.getTotalPrincipalOutstanding();
            }

            CashflowSchedule cashflowSchedule = new CashflowSchedule();
            cashflowSchedule.setCashflow(cashflow);

            // Set dates
            cashflowSchedule.setFromDate(scheduleDate.getDate());
            if (previousCashflowSchedule != null) {
                cashflowSchedule.setFromDate(previousCashflowSchedule.getToDate());
            }
            cashflowSchedule.setToDate(scheduleDate.getDate());
            cashflowSchedule.setDateType(scheduleDate.getType());

            // Set Interest details, Calculate Interest Outflow
            addInterestDetailsToCashflowSchedule(cashflowSchedule, interestDetailsInput, dayCountConvention, principalOutstanding);
            double interestOutflow = cashflowSchedule.getInterestOutflow();

            // Calculate Principal Repayment and Cash Movement
            if (scheduleDate.getType() == DateType.ORIGINATION) {
                cashflowSchedule.setPrincipalInflow(-principalAmount);
                cashMovement = -principalAmount;
            } else if (scheduleDate.getType() == DateType.PREPAYMENT) {
                principalRepayment = getPrepaymentAmountForDate(prepaymentDetailsInput, scheduleDate.getDate());
                outstandingInterestOutflow += interestOutflow;
                cashMovement = principalRepayment;
            } else if (scheduleDate.getType() == DateType.INTEREST || scheduleDate.getType() == DateType.INTEREST_AND_MATURITY) {
                if (!isInterestAccrued) {
                    cashMovement = interestOutflow + outstandingInterestOutflow;
                }
                outstandingInterestOutflow = 0;
            } else if (scheduleDate.getType() == DateType.INTEREST_AND_PREPAYMENT) {
                principalRepayment = getPrepaymentAmountForDate(prepaymentDetailsInput, scheduleDate.getDate());
                cashMovement = principalRepayment;
                if (!isInterestAccrued) {
                    cashMovement += interestOutflow + outstandingInterestOutflow;
                }
                outstandingInterestOutflow = 0;
            } else if (scheduleDate.getType() == DateType.MATURITY) {

            }

            // Set Principal outstanding and Cash Movement, and Repayment
            principalAmount -= principalRepayment;
            if (isInterestAccrued) {
                principalAmount += interestOutflow;
            }
            cashflowSchedule.setTotalPrincipalOutstanding(principalAmount);
            if (scheduleDate.getType() == DateType.MATURITY || scheduleDate.getType() == DateType.INTEREST_AND_MATURITY) {
                cashMovement += principalAmount;
            }
            cashflowSchedule.setTotalCashMovement(cashMovement);
            cashflowSchedule.setPrincipalRepayment(principalRepayment);

            // Set Partial Period, Discounting factor and Present Value
            addPresentValueDetailsToCashflowSchedule(cashflowSchedule, dayCountConvention, cashMovement);

            schedules.add(cashflowSchedule);
            previousCashflowSchedule = cashflowSchedule;
        }

        // add schedules to Cashflow
        cashflow.setSchedules(schedules);
    }

    private void addCashflowSummaryToCashflow(Cashflow cashflow, List<DebtModelInputDto> inputs) {
        Optional<GeneralDetails> generalDetailsInput = getGeneralDetailsFromInputs(inputs);
        if (!generalDetailsInput.isPresent()) {
            return;
        }

        LocalDate valuationDate = generalDetailsInput.get().getValuationDate();
        // Calculate Present value sum of Future cashflows
        double presentValueSum = cashflow.getSchedules().stream()
                .filter(cashflowSchedule -> cashflowSchedule.getToDate().isAfter(valuationDate))
                .mapToDouble(CashflowSchedule::getPresentValue).sum();
        cashflow.setPresentValueSum(presentValueSum);

        // Calculate % par
        double principalOutstanding = generalDetailsInput.get().getPrincipalAmount() * generalDetailsInput.get().getPrincipalOutstanding() / 100;
        if (principalOutstanding != 0) {
            cashflow.setPercentagePar(presentValueSum * 100 / principalOutstanding);
        }
    }

    private void addInterestDetailsToCashflowSchedule(CashflowSchedule cashflowSchedule, Optional<InterestDetails> interestDetails, DayCountConvention dayCountConvention, double principalOutstanding) {
        // TODO: This would vary based on the Base Rate Curve
        double totalInterestRate = getTotalInterestRate(interestDetails, cashflowSchedule.getToDate());
        double interestBaseRate = getInterestBaseRate(interestDetails, cashflowSchedule.getToDate());
        double interestBaseRateSpread = getInterestBaseRateSpread(interestDetails, cashflowSchedule.getToDate());
        double interestOutflow = CashflowUtil.getInterestOutflow(cashflowSchedule.getFromDate(), cashflowSchedule.getToDate(), dayCountConvention, principalOutstanding, totalInterestRate);
        cashflowSchedule.setInterestOutflow(interestOutflow);
        cashflowSchedule.setBaseRate(interestBaseRate);
        cashflowSchedule.setBaseRateSpread(interestBaseRateSpread);
        cashflowSchedule.setTotalInterestRate(totalInterestRate);

        // TODO:
        cashflowSchedule.setYearFrac(CashflowUtil.getPartialPeriod(cashflowSchedule.getFromDate(), cashflowSchedule.getToDate(), dayCountConvention));
    }

    private void addPresentValueDetailsToCashflowSchedule(CashflowSchedule cashflowSchedule, DayCountConvention dayCountConvention, double cashMovement) {
        Cashflow cashflow = cashflowSchedule.getCashflow();
        LocalDate date = cashflowSchedule.getToDate();

        double partialPeriod = CashflowUtil.getPartialPeriod(cashflow.getValuationDate(), date, dayCountConvention);
        double discountingFactor = CashflowUtil.getDiscountingFactor(cashflow.getDiscountRate(), partialPeriod);
        double presentValue = CashflowUtil.getPresentValue(cashMovement, discountingFactor, date, cashflow.getValuationDate());

        cashflowSchedule.setPartialPeriod(partialPeriod);
        cashflowSchedule.setDiscountingFactor(discountingFactor);
        cashflowSchedule.setPresentValue(presentValue);
    }

    private double getPrepaymentAmountForDate(Optional<PrepaymentDetails> input, LocalDate date) {
        double amount = 0;
        if (!input.isPresent()) {
            return amount;
        }
        Set<PaymentSchedule> schedules = input.get().getPaymentSchedules();
        return schedules.stream().filter(schedule -> schedule.getDate().isEqual(date)).findFirst().map(PaymentSchedule::getAmount).orElse(0.0);
    }

    private double getTotalInterestRate(Optional<InterestDetails> input, LocalDate date) {
        if (!input.isPresent()) {
            return 0.0;
        }
        BaseRate baseRate = input.get().getBaseRate();
        BaseRateCurve baseRateCurve = input.get().getBaseRateCurve();
        return input.map(InterestDetails::getTotalInterestRate).orElse(0.0);
    }

    private double getInterestBaseRate(Optional<InterestDetails> input, LocalDate date) {
        return input.map(InterestDetails::getBaseRateValue).orElse(0.0);
    }

    private double getInterestBaseRateSpread(Optional<InterestDetails> input, LocalDate date) {
        return input.map(InterestDetails::getBaseRateSpread).orElse(0.0);
    }

    private Set<CashflowScheduleDate> getCashflowScheduleDates(List<DebtModelInputDto> inputs) {
        Set<CashflowScheduleDate> scheduleDates = new HashSet<>();

        // Origination & Maturity dates
        Optional<GeneralDetails> generalDetails = getGeneralDetailsFromInputs(inputs);
        if (!generalDetails.isPresent()) {
            return scheduleDates;
        }
        LocalDate originationDate = generalDetails.get().getOriginationDate();
        scheduleDates.add(new CashflowScheduleDate(originationDate, DateType.ORIGINATION));

        LocalDate maturityDate = generalDetails.get().getMaturityDate();

        // Coupon dates
        Optional<InterestDetails> interestDetails = getInterestDetailsFromInputs(inputs);
        if (interestDetails.isPresent()) {
            Set<LocalDate> couponDates = interestDetails.get().getCouponDates();
            AtomicBoolean hasMaturityDateCoupon = new AtomicBoolean(false);
            scheduleDates.addAll(
                    couponDates.stream()
                            .map(couponDate -> {
                                if (couponDate.isEqual(maturityDate)) {
                                    hasMaturityDateCoupon.set(true);
                                }
                                return new CashflowScheduleDate(couponDate, couponDate.isEqual(maturityDate) ? DateType.INTEREST_AND_MATURITY : DateType.INTEREST);
                            })
                            .collect(Collectors.toSet())
            );
            if (!hasMaturityDateCoupon.get()) {
                scheduleDates.add(new CashflowScheduleDate(maturityDate, DateType.MATURITY));
            }
        }

        // Prepayment dates
        Optional<PrepaymentDetails> prepaymentDetails = getPrepaymentDetailsFromInputs(inputs);
        if (prepaymentDetails.isPresent()) {
            Set<PaymentSchedule> schedules = prepaymentDetails.get().getPaymentSchedules();
            schedules.forEach(schedule -> {
                LocalDate date = schedule.getDate();
                DateType type = DateType.PREPAYMENT;
                // check if this date is already in the schedule
                Optional<CashflowScheduleDate> existingScheduleDate = scheduleDates.stream().filter(scheduleDate -> scheduleDate.getDate().isEqual(date)).findFirst();
                if (existingScheduleDate.isPresent() && existingScheduleDate.get().getType().equals(DateType.INTEREST)) {
                    scheduleDates.remove(existingScheduleDate.get());
                    type = DateType.INTEREST_AND_PREPAYMENT;
                }
                scheduleDates.add(new CashflowScheduleDate(date, type));
            });
        }

        // Sort the set chronologically
        return scheduleDates
                .stream()
                .sorted(Comparator.comparing(CashflowScheduleDate::getDate))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Optional<GeneralDetails> getGeneralDetailsFromInputs(List<DebtModelInputDto> inputs) {
        return inputs.stream().filter(input -> input.getInputType() == DebtModelInput.GENERAL_DETAILS).map(input -> modelMapper.map(input.getPayload(), GeneralDetails.class)).findFirst();
    }

    private Optional<InterestDetails> getInterestDetailsFromInputs(List<DebtModelInputDto> inputs) {
        return inputs.stream().filter(input -> input.getInputType() == DebtModelInput.INTEREST_DETAILS).map(input -> modelMapper.map(input.getPayload(), InterestDetails.class)).findFirst();
    }

    private Optional<PrepaymentDetails> getPrepaymentDetailsFromInputs(List<DebtModelInputDto> inputs) {
        return inputs.stream().filter(input -> input.getInputType() == DebtModelInput.PREPAYMENT_DETAILS).map(input -> modelMapper.map(input.getPayload(), PrepaymentDetails.class)).findFirst();
    }
}
