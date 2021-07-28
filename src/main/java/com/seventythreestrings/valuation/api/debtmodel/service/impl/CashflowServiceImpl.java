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
        List<InterestDetails> interestDetailsInput = getInterestDetailsFromInputs(inputs);
        Optional<PrepaymentDetails> prepaymentDetailsInput = getPrepaymentDetailsFromInputs(inputs);
        List<DealFees> dealFees = getDealFeesFromInputs(inputs);
        List<InterestUndrawnCapital> interestUndrawnCapitals = getInterestUndrawnCapitalFromInputs(inputs);
        List<Skims> skims = getSkimsFromInputs(inputs);
        if (!generalDetailsInput.isPresent()) {
            return;
        }
        double principalAmount = generalDetailsInput.get().getPrincipalAmount();
        double percentageOfCalledDown = generalDetailsInput.get().getPercentageOfCalledDown();
        double calledDownCapital = principalAmount * percentageOfCalledDown / 100;

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

            boolean isInterestAccrued = getIsInterestAccrued(interestDetailsInput, cashflowSchedule.getToDate());

            // Set Interest details, Calculate Interest Outflow
            addInterestDetailsToCashflowSchedule(cashflowSchedule, interestDetailsInput, dayCountConvention, principalOutstanding);
            double interestOutflow = cashflowSchedule.getInterestOutflow();

            // Set Deal Fees, Calculate Deal Fee Outflow
            addDealFeesToCashflowSchedule(cashflowSchedule, dealFees, dayCountConvention, principalOutstanding, principalAmount);
            double dealFeesOutflow = cashflowSchedule.getDealFeesOutflow();

            // Set Interest Undrawn Capitals, Calculate Interest Undrawn Capitals Outflow
            addInterestUndrawnCapitalToCashflowSchedule(cashflowSchedule, interestUndrawnCapitals, dayCountConvention, calledDownCapital);
            double interestUndrawnCapitalOutflow = cashflowSchedule.getInterestUndrawnCapitalOutflow();

            // Set Skims, Calculate Skims Outflow
            addSkimsToCashflowSchedule(cashflowSchedule, skims, dayCountConvention, principalOutstanding, principalAmount);
            double skimsOutflow = cashflowSchedule.getSkimsOutflow();

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
            } else if (scheduleDate.getType() == DateType.DEALFEES || scheduleDate.getType() == DateType.DEALFEES_AND_MATURITY) {
                cashMovement = dealFeesOutflow + outstandingInterestOutflow;
            } else if (scheduleDate.getType() == DateType.INTEREST_UNDRAWN_CAPITAL || scheduleDate.getType() == DateType.INTEREST_UNDRAWN_CAPITAL_AND_MATURITY) {
                cashMovement = interestUndrawnCapitalOutflow + outstandingInterestOutflow;
            } else if (scheduleDate.getType() == DateType.SKIMS || scheduleDate.getType() == DateType.SKIMS_AND_MATURITY) {
                cashMovement = skimsOutflow + outstandingInterestOutflow;
            } else if (scheduleDate.getType() == DateType.MATURITY) {

            }

            // Set Principal outstanding and Cash Movement, and Repayment
            principalAmount -= principalRepayment;
            if (isInterestAccrued) {
                principalAmount += interestOutflow;
            }
            cashflowSchedule.setTotalPrincipalOutstanding(principalAmount);
            if (scheduleDate.getType() == DateType.MATURITY || scheduleDate.getType() == DateType.INTEREST_AND_MATURITY || scheduleDate.getType() == DateType.DEALFEES_AND_MATURITY || scheduleDate.getType() == DateType.INTEREST_UNDRAWN_CAPITAL_AND_MATURITY || scheduleDate.getType() == DateType.SKIMS_AND_MATURITY) {
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

        // Add irr to Cashflow
        double irr = getInternalRateOfReturn(cashflow, generalDetailsInput.get().getPrincipalAmount());
        cashflow.setInternalRateOfReturn(irr);
    }

    private double getSumOfDiscountedPV(Set<CashflowSchedule> schedules, LocalDate valuationDate, double irr) {
        double spv = 0; // spv = sum of present value
        for (CashflowSchedule schedule: schedules) {
            if (schedule.getToDate().isAfter(schedule.getFromDate())) {
                double cashMovement = schedule.getTotalCashMovement();
                LocalDate date = schedule.getToDate();

                double discountingFactor = CashflowUtil.getDiscountingFactor(irr, schedule.getPartialPeriod());
                double presentValue = CashflowUtil.getPresentValue(cashMovement, discountingFactor, date, valuationDate);
                spv += presentValue;
            }
        }
        return spv;
    }

    private double getInternalRateOfReturn(Cashflow cashflow, double principalAmount) {
        Set<CashflowSchedule> schedules = cashflow.getSchedules();
        LocalDate valuationDate = cashflow.getValuationDate();
        // Initial approximate irr
        double irr = schedules.stream()
                .filter(cashflowSchedule -> cashflowSchedule.getToDate().isAfter(valuationDate))
                .mapToDouble(CashflowSchedule::getTotalInterestRate).average().orElse(0.0);
        double spv = getSumOfDiscountedPV(schedules, valuationDate, irr);
        if (spv > principalAmount) {
            while(spv > principalAmount) {
                System.out.println("irr "+ irr + " spv " + spv);
                irr += 0.001;
                spv = getSumOfDiscountedPV(schedules, valuationDate, irr);
            }
            return irr;
        } else if (principalAmount > spv) {
            while(principalAmount > spv) {
                System.out.println("irr "+ irr + " spv " + spv);
                irr -= 0.001;
                spv = getSumOfDiscountedPV(schedules, valuationDate, irr);
            }
            return irr;
        }
        return irr;
    }

    private void addInterestDetailsToCashflowSchedule(CashflowSchedule cashflowSchedule, List<InterestDetails> interestDetails, DayCountConvention dayCountConvention, double principalOutstanding) {
        // TODO: This would vary based on the Base Rate Curve
        Optional<InterestDetails> interestDetail = getInterestDetailsByDate(interestDetails, cashflowSchedule.getToDate());
        double totalInterestRate = getTotalInterestRate(interestDetail, cashflowSchedule.getToDate());
        double interestBaseRate = getInterestBaseRate(interestDetail, cashflowSchedule.getToDate());
        double interestBaseRateSpread = getInterestBaseRateSpread(interestDetail, cashflowSchedule.getToDate());
        double interestOutflow = CashflowUtil.getInterestOutflow(cashflowSchedule.getFromDate(), cashflowSchedule.getToDate(), dayCountConvention, principalOutstanding, totalInterestRate);
        cashflowSchedule.setInterestOutflow(interestOutflow);
        cashflowSchedule.setBaseRate(interestBaseRate);
        cashflowSchedule.setBaseRateSpread(interestBaseRateSpread);
        cashflowSchedule.setTotalInterestRate(totalInterestRate);

        // TODO:
        cashflowSchedule.setYearFrac(CashflowUtil.getPartialPeriod(cashflowSchedule.getFromDate(), cashflowSchedule.getToDate(), dayCountConvention));
    }

    private void addDealFeesToCashflowSchedule(CashflowSchedule cashflowSchedule, List<DealFees> dealFees, DayCountConvention dayCountConvention, double principalOutstanding, double principalAmount) {
        Optional<DealFees> dealFee = getDealFeesByDate(dealFees, cashflowSchedule.getToDate());
        double annualFeePercentage = getAnnualFeePercentage(dealFee, cashflowSchedule.getToDate());
        double amount = 0.0;
        if (dealFee.isPresent()) {
            FeeBase feeBase = dealFee.get().getFeeBase();
            if (feeBase == FeeBase.COMMITTED_CAPITAL) {
                amount = principalAmount;
            } else if (feeBase == FeeBase.CALLED_DOWN_CAPITAL) {
                amount = principalOutstanding;
            }
        }
        double dealFeesOutflow = CashflowUtil.getInterestOutflow(cashflowSchedule.getFromDate(), cashflowSchedule.getToDate(), dayCountConvention, amount, annualFeePercentage);
        cashflowSchedule.setDealFeesOutflow(dealFeesOutflow);
        cashflowSchedule.setAnnualFeePercentage(annualFeePercentage);
    }

    private void addInterestUndrawnCapitalToCashflowSchedule(CashflowSchedule cashflowSchedule, List<InterestUndrawnCapital> interestUndrawnCapitals, DayCountConvention dayCountConvention, double calledDownCapital) {
        Optional<InterestUndrawnCapital> interestUndrawnCapital = getInterestUndrawnCapitalByDate(interestUndrawnCapitals, cashflowSchedule.getToDate());
        double interestUndrawnPercentage = getInterestUndrawnPercentage(interestUndrawnCapital, cashflowSchedule.getToDate());
        double interestUndrawnCapitalOutflow = CashflowUtil.getInterestOutflow(cashflowSchedule.getFromDate(), cashflowSchedule.getToDate(), dayCountConvention, calledDownCapital, interestUndrawnPercentage);
        cashflowSchedule.setInterestUndrawnCapitalOutflow(interestUndrawnCapitalOutflow);
        cashflowSchedule.setInterestUndrawnPercentage(interestUndrawnPercentage);
    }

    private void addSkimsToCashflowSchedule(CashflowSchedule cashflowSchedule, List<Skims> skims, DayCountConvention dayCountConvention, double principalOutstanding, double principalAmount) {
        Optional<Skims> skim = getSkimsByDate(skims, cashflowSchedule.getToDate());
        double skimPercentage = getSkimPercentage(skim, cashflowSchedule.getToDate());
        double amount = 0.0;
        if (skim.isPresent()) {
            SkimBase skimBase = skim.get().getSkimBase();
            if (skimBase == SkimBase.TERM_LOAN) {
                amount = principalOutstanding;
            } else if (skimBase == SkimBase.REVOLVER) {
                amount = principalAmount;
            }
        }
        double skimsOutflow = CashflowUtil.getInterestOutflow(cashflowSchedule.getFromDate(), cashflowSchedule.getToDate(), dayCountConvention, amount, skimPercentage);
        cashflowSchedule.setSkimsOutflow(skimsOutflow);
        cashflowSchedule.setSkimPercentage(skimPercentage);
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

    private Optional<InterestDetails> getInterestDetailsByDate(List<InterestDetails> inputs, LocalDate date) {
        // get single interest details
        // regime start date and end dates are inclusive
        return inputs.stream().filter(input -> !input.getRegimeStartDate().isAfter(date)).
                filter(input -> !input.getRegimeEndDate().isBefore(date)).findFirst();
    }

    private Optional<DealFees> getDealFeesByDate(List<DealFees> inputs, LocalDate date) {
        // get single deal fees
        // regime start date and end dates are inclusive
        return inputs.stream().filter(input -> !input.getRegimeStartDate().isAfter(date)).
                filter(input -> !input.getRegimeEndDate().isBefore(date)).findFirst();
    }

    private Optional<InterestUndrawnCapital> getInterestUndrawnCapitalByDate(List<InterestUndrawnCapital> inputs, LocalDate date) {
        // get single interest details
        // regime start date and end dates are inclusive
        return inputs.stream().filter(input -> !input.getRegimeStartDate().isAfter(date)).
                filter(input -> !input.getRegimeEndDate().isBefore(date)).findFirst();
    }

    private Optional<Skims> getSkimsByDate(List<Skims> inputs, LocalDate date) {
        // get single interest details
        // regime start date and end dates are inclusive
        return inputs.stream().filter(input -> !input.getRegimeStartDate().isAfter(date)).
                filter(input -> !input.getRegimeEndDate().isBefore(date)).findFirst();
    }

    private boolean getIsInterestAccrued(List<InterestDetails> input, LocalDate date) {
        Optional<InterestDetails> interestDetail = getInterestDetailsByDate(input , date);
        return interestDetail.map(interestDetails -> interestDetails.getInterestPaidOrAccrued().equals(InterestType.ACCRUED)).orElse(false);
    }

    private double getAnnualFeePercentage(Optional<DealFees> input, LocalDate date) {
        if (!input.isPresent()) {
            return 0.0;
        }
        return input.map(DealFees::getAnnualFeePercentage).orElse(0.0);
    }

    private double getInterestUndrawnPercentage(Optional<InterestUndrawnCapital> input, LocalDate date) {
        if (!input.isPresent()) {
            return 0.0;
        }
        return input.map(InterestUndrawnCapital::getInterestUndrawnPercentage).orElse(0.0);
    }

    private double getSkimPercentage(Optional<Skims> input, LocalDate date) {
        if (!input.isPresent()) {
            return 0.0;
        }
        return input.map(Skims::getSkimPercentage).orElse(0.0);
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
        List<InterestDetails> interestDetails = getInterestDetailsFromInputs(inputs);
        AtomicBoolean hasMaturityDateCoupon = new AtomicBoolean(false);
        for (InterestDetails interestDetail : interestDetails) {
                Set<LocalDate> couponDates = interestDetail.getCouponDates();
                scheduleDates.addAll(
                        couponDates.stream()
                                .map(couponDate -> {
                                    if (couponDate.isEqual(maturityDate)) {
                                        hasMaturityDateCoupon.set(true);
                                    }
                                    return new CashflowScheduleDate(couponDate, couponDate.isEqual(maturityDate) ? DateType.INTEREST_AND_MATURITY : DateType.INTEREST);
                                })
                                .collect(Collectors.toSet()));
        }

        // Deal fees
        List<DealFees> dealFees = getDealFeesFromInputs(inputs);
        for (DealFees dealFee : dealFees) {
            Set<LocalDate> couponDates = dealFee.getCouponDates();
            scheduleDates.addAll(
                    couponDates.stream()
                            .map(couponDate -> {
                                if (couponDate.isEqual(maturityDate)) {
                                    hasMaturityDateCoupon.set(true);
                                }
                                return new CashflowScheduleDate(couponDate, couponDate.isEqual(maturityDate) ? DateType.DEALFEES_AND_MATURITY : DateType.DEALFEES);
                            })
                            .collect(Collectors.toSet()));
        }

        // Undrawn Capital
        List<InterestUndrawnCapital> interestUndrawnCapitals = getInterestUndrawnCapitalFromInputs(inputs);
        for (InterestUndrawnCapital interestUndrawnCapital : interestUndrawnCapitals) {
            Set<LocalDate> couponDates = interestUndrawnCapital.getCouponDates();
            scheduleDates.addAll(
                    couponDates.stream()
                            .map(couponDate -> {
                                if (couponDate.isEqual(maturityDate)) {
                                    hasMaturityDateCoupon.set(true);
                                }
                                return new CashflowScheduleDate(couponDate, couponDate.isEqual(maturityDate) ? DateType.INTEREST_UNDRAWN_CAPITAL_AND_MATURITY : DateType.INTEREST_UNDRAWN_CAPITAL);
                            })
                            .collect(Collectors.toSet()));
        }

        // Skims
        List<Skims> skims = getSkimsFromInputs(inputs);
        for (Skims skim : skims) {
            Set<LocalDate> couponDates = skim.getCouponDates();
            scheduleDates.addAll(
                    couponDates.stream()
                            .map(couponDate -> {
                                if (couponDate.isEqual(maturityDate)) {
                                    hasMaturityDateCoupon.set(true);
                                }
                                return new CashflowScheduleDate(couponDate, couponDate.isEqual(maturityDate) ? DateType.SKIMS_AND_MATURITY : DateType.SKIMS);
                            })
                            .collect(Collectors.toSet()));
        }

        if (!hasMaturityDateCoupon.get()) {
            scheduleDates.add(new CashflowScheduleDate(maturityDate, DateType.MATURITY));
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

    private List<InterestDetails> getInterestDetailsFromInputs(List<DebtModelInputDto> inputs) {
        return inputs.stream().filter(input -> input.getInputType() == DebtModelInput.INTEREST_DETAILS).map(input -> Arrays.asList(modelMapper.map(input.getPayload(), InterestDetails[].class))).findFirst().orElse(new ArrayList<InterestDetails>());
    }

    private Optional<PrepaymentDetails> getPrepaymentDetailsFromInputs(List<DebtModelInputDto> inputs) {
        return inputs.stream().filter(input -> input.getInputType() == DebtModelInput.PREPAYMENT_DETAILS).map(input -> modelMapper.map(input.getPayload(), PrepaymentDetails.class)).findFirst();
    }

    private List<DealFees> getDealFeesFromInputs(List<DebtModelInputDto> inputs) {
        return inputs.stream().filter(input -> input.getInputType() == DebtModelInput.DEAL_FEES).map(input -> Arrays.asList(modelMapper.map(input.getPayload(), DealFees[].class))).findFirst().orElse(new ArrayList<DealFees>());
    }

    private List<InterestUndrawnCapital> getInterestUndrawnCapitalFromInputs(List<DebtModelInputDto> inputs) {
        return inputs.stream().filter(input -> input.getInputType() == DebtModelInput.INTEREST_UNDRAWN_CAPITAL).map(input -> Arrays.asList(modelMapper.map(input.getPayload(), InterestUndrawnCapital[].class))).findFirst().orElse(new ArrayList<InterestUndrawnCapital>());
    }

    private List<Skims> getSkimsFromInputs(List<DebtModelInputDto> inputs) {
        return inputs.stream().filter(input -> input.getInputType() == DebtModelInput.SKIMS).map(input -> Arrays.asList(modelMapper.map(input.getPayload(), Skims[].class))).findFirst().orElse(new ArrayList<Skims>());
    }
}
