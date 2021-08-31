package com.seventythreestrings.valuation.api.debtmodel.service.impl;

import com.seventythreestrings.valuation.api.debtmodel.dto.*;
import com.seventythreestrings.valuation.api.debtmodel.enums.*;
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
import java.util.concurrent.atomic.AtomicReference;
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
            cashflow.setExitDate(generalDetails.get().getExitDate());
            cashflow.setDiscountRate(generalDetails.get().getDiscountRate());
            cashflow.setDayCountConvention(generalDetails.get().getDayCountConvention());
        }
    }

    private void addCashflowSchedulesToCashflow(Cashflow cashflow, List<DebtModelInputDto> inputs) {
        Set<CashflowSchedule> schedules = new LinkedHashSet<>();
        Set<CashflowScheduleDateDto> scheduleDates = getCashflowScheduleDates(inputs);
        Optional<GeneralDetails> generalDetailsInput = getGeneralDetailsFromInputs(inputs);
        List<InterestDetails> interestDetailsInput = getInterestDetailsFromInputs(inputs);
        Optional<RepaymentDetails> prepaymentDetailsInput = getPrepaymentDetailsFromInputs(inputs);
        List<DealFees> dealFeesInput = getDealFeesFromInputs(inputs);
        List<InterestUndrawnCapital> interestUndrawnCapitalsInput = getInterestUndrawnCapitalFromInputs(inputs);
        List<Skims> skimsInput = getSkimsFromInputs(inputs);
        List<CallPremium> callPremiumInput = getCallPremiumFromInputs(inputs);
        List<Object> customizableCashflowInput = getCustomizableCashflowFromInputs(inputs);

        if (!generalDetailsInput.isPresent()) {
            return;
        }
        double principalAmount = generalDetailsInput.get().getPrincipalAmount();
        double percentageOutstanding = generalDetailsInput.get().getPrincipalOutstanding();
        double committedCapital = principalAmount;
        double calledDownCapital = principalAmount * percentageOutstanding / 100;
        double undrawnCapital = principalAmount * ((100 - percentageOutstanding) / 100);

        double principalAmountVarying = calledDownCapital;

        DayCountConvention dayCountConvention = cashflow.getDayCountConvention();
        if (dayCountConvention == null) {
            dayCountConvention = DayCountConvention.ACTUAL_BY_ACTUAL;
        }


        CashflowSchedule previousCashflowSchedule = null;
        double outstandingInterestOutflow = 0;
        for (CashflowScheduleDateDto scheduleDate: scheduleDates) {
            CashflowSchedule cashflowSchedule = new CashflowSchedule();
            cashflowSchedule.setCashflow(cashflow);

            double principalRepayment = 0;
            double cashMovement = 0;
            double principalOutstanding = 0;
            if (previousCashflowSchedule != null) {
                principalOutstanding = previousCashflowSchedule.getTotalPrincipalOutstanding();
                cashflowSchedule.setOpeningPrincipalOutstanding(principalOutstanding);
            }

            // Set dates
            cashflowSchedule.setFromDate(scheduleDate.getDate());
            if (previousCashflowSchedule != null) {
                cashflowSchedule.setFromDate(previousCashflowSchedule.getToDate());
            }
            cashflowSchedule.setToDate(scheduleDate.getDate());
            cashflowSchedule.setDateType(scheduleDate.getType());

            String dateTypeString = scheduleDate.getType().name();
            boolean isInterestAccrued = false;
            double interestOutflow = 0;
            double dealFeesOutflow = 0;
            double interestUndrawnCapitalOutflow = 0;
            double skimsOutflow = 0;
            double customCashflowAmount = 0;
            boolean isCustomAccrued = false;

            cashflowSchedule.setCommittedCapital(committedCapital);
            cashflowSchedule.setCalledDownCapital(calledDownCapital);
            cashflowSchedule.setUndrawnCapital(undrawnCapital);

            // For Interest Details
            if (dateTypeString.contains("INTEREST")) {
                isInterestAccrued = getIsInterestAccrued(interestDetailsInput, cashflowSchedule.getToDate());
                // Set Interest details, Calculate Interest Outflow
                addInterestDetailsToCashflowSchedule(cashflowSchedule, interestDetailsInput, dayCountConvention, principalOutstanding);
                interestOutflow = cashflowSchedule.getInterestOutflow();
            }

            // For Deal Fees
            if (dateTypeString.contains("DEALFEES")) {
                // Set Deal Fees, Calculate Deal Fee Outflow
                addDealFeesToCashflowSchedule(cashflowSchedule, dealFeesInput, dayCountConvention);
                dealFeesOutflow = cashflowSchedule.getDealFeesOutflow();
            }

            // For Undrawn Capitals
            if (dateTypeString.contains("UNDRAWN_CAPITAL")) {
                // Set Interest Undrawn Capitals, Calculate Interest Undrawn Capitals Outflow
                addInterestUndrawnCapitalToCashflowSchedule(cashflowSchedule, interestUndrawnCapitalsInput, dayCountConvention);
                interestUndrawnCapitalOutflow = cashflowSchedule.getInterestUndrawnCapitalOutflow();
            }

            // For Skims
            if (dateTypeString.contains("SKIMS")) {
                // Set Skims, Calculate Skims Outflow
                addSkimsToCashflowSchedule(cashflowSchedule, skimsInput, dayCountConvention);
                skimsOutflow = cashflowSchedule.getSkimsOutflow();
            }

            // For Customizable Cashflow
            if (dateTypeString.contains("CUSTOM")) {
                // Set Customizable Cashflow, Calculate Specific Dates or Pre existing Dates or Excel Dates Amount
                isCustomAccrued = getIsCustomAccrued(customizableCashflowInput, cashflowSchedule.getToDate(), dateTypeString);
                addCustomizableCashflowToCashflowSchedule(cashflowSchedule, customizableCashflowInput, dayCountConvention, dateTypeString);
                customCashflowAmount = cashflowSchedule.getCustomCashflowAmount();
            }

            // Calculate Principal Repayment and Cash Movement
            switch (scheduleDate.getType()) {
                case ORIGINATION:
                    cashflowSchedule.setPrincipalInflow(-principalAmountVarying);
                    cashMovement = -principalAmountVarying;
                    break;
                case REPAYMENT:
                    principalRepayment = getPrepaymentAmountForDate(prepaymentDetailsInput, scheduleDate.getDate());
                    double callPremiumAmount = getCallPremiumAmountForDate(callPremiumInput, scheduleDate.getDate(), principalRepayment, cashflowSchedule);
                    outstandingInterestOutflow += interestOutflow;
                    if (previousCashflowSchedule != null) {
                        if (previousCashflowSchedule.getDateType().name().contains("CUSTOM_SPECIFIC")) {
                            outstandingInterestOutflow += customCashflowAmount;
                        }
                    }
                    cashMovement = principalRepayment + callPremiumAmount;
                    break;
                case INTEREST:
                case CUSTOM_SPECIFIC:
                case INTEREST_AND_CUSTOM_SPECIFIC:
                case INTEREST_AND_MATURITY:
                case CUSTOM_SPECIFIC_AND_MATURITY:
                case INTEREST_AND_CUSTOM_SPECIFIC_AND_MATURITY:
                    if (!isInterestAccrued) {
                        cashMovement = interestOutflow + outstandingInterestOutflow;
                    }
                    if (!isCustomAccrued) {
                        cashMovement = interestOutflow;
                        if (isInterestAccrued) {
                            cashMovement += outstandingInterestOutflow;
                        }
                    }
                    outstandingInterestOutflow = 0;
                    break;
                case INTEREST_AND_REPAYMENT:
                case REPAYMENT_AND_DEALFEES:
                case REPAYMENT_AND_UNDRAWN_CAPITAL:
                case REPAYMENT_AND_SKIMS:
                case REPAYMENT_AND_CUSTOM_SPECIFIC:
                case REPAYMENT_AND_CUSTOM_PRE:
                case REPAYMENT_AND_CUSTOM_EXCEL:
                case INTEREST_AND_REPAYMENT_AND_DEALFEES:
                case INTEREST_AND_REPAYMENT_AND_UNDRAWN_CAPITAL:
                case INTEREST_AND_REPAYMENT_AND_SKIMS:
                case INTEREST_AND_REPAYMENT_AND_CUSTOM_SPECIFIC:
                case INTEREST_AND_REPAYMENT_AND_CUSTOM_PRE:
                case INTEREST_AND_REPAYMENT_AND_CUSTOM_EXCEL:
                case REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL:
                case REPAYMENT_AND_DEALFEES_AND_SKIMS:
                case REPAYMENT_AND_DEALFEES_AND_CUSTOM_SPECIFIC:
                case REPAYMENT_AND_DEALFEES_AND_CUSTOM_PRE:
                case REPAYMENT_AND_DEALFEES_AND_CUSTOM_EXCEL:
                case REPAYMENT_AND_UNDRAWN_CAPITAL_AND_SKIMS:
                case REPAYMENT_AND_UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC:
                case REPAYMENT_AND_UNDRAWN_CAPITAL_AND_CUSTOM_PRE:
                case REPAYMENT_AND_UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL:
                case REPAYMENT_AND_SKIMS_AND_CUSTOM_SPECIFIC:
                case REPAYMENT_AND_SKIMS_AND_CUSTOM_PRE:
                case REPAYMENT_AND_SKIMS_AND_CUSTOM_EXCEL:
                case INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL:
                case INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_SKIMS:
                case INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_CUSTOM_SPECIFIC:
                case INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_CUSTOM_PRE:
                case INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_CUSTOM_EXCEL:
                case INTEREST_AND_REPAYMENT_AND_UNDRAWN_CAPITAL_AND_SKIMS:
                case INTEREST_AND_REPAYMENT_AND_UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC:
                case INTEREST_AND_REPAYMENT_AND_UNDRAWN_CAPITAL_AND_CUSTOM_PRE:
                case INTEREST_AND_REPAYMENT_AND_UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL:
                case INTEREST_AND_REPAYMENT_AND_SKIMS_AND_CUSTOM_SPECIFIC:
                case INTEREST_AND_REPAYMENT_AND_SKIMS_AND_CUSTOM_PRE:
                case INTEREST_AND_REPAYMENT_AND_SKIMS_AND_CUSTOM_EXCEL:
                case REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS:
                case REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC:
                case REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_PRE:
                case REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL:
                case REPAYMENT_AND_DEALFEES_AND_SKIMS_AND_CUSTOM_SPECIFIC:
                case REPAYMENT_AND_DEALFEES_AND_SKIMS_AND_CUSTOM_PRE:
                case REPAYMENT_AND_DEALFEES_AND_SKIMS_AND_CUSTOM_EXCEL:
                case REPAYMENT_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC:
                case REPAYMENT_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE:
                case REPAYMENT_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL:
                case INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS:
                case INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC:
                case INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_PRE:
                case INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL:
                case INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_SKIMS_AND_CUSTOM_SPECIFIC:
                case INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_SKIMS_AND_CUSTOM_PRE:
                case INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_SKIMS_AND_CUSTOM_EXCEL:
                case INTEREST_AND_REPAYMENT_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC:
                case INTEREST_AND_REPAYMENT_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE:
                case INTEREST_AND_REPAYMENT_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL:
                case REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC:
                case REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE:
                case REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL:
                case INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC:
                case INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE:
                case INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL:
                    principalRepayment = getPrepaymentAmountForDate(prepaymentDetailsInput, scheduleDate.getDate());
                    callPremiumAmount = getCallPremiumAmountForDate(callPremiumInput, scheduleDate.getDate(), principalRepayment, cashflowSchedule);
                    cashMovement = principalRepayment + callPremiumAmount + dealFeesOutflow + interestUndrawnCapitalOutflow + skimsOutflow + customCashflowAmount;
                    if (!isInterestAccrued) {
                        cashMovement += interestOutflow + outstandingInterestOutflow;
                    }
                    if (isCustomAccrued) {
                        cashMovement -= customCashflowAmount;
                    }
                    if (!isCustomAccrued && isInterestAccrued) {
                        cashMovement += outstandingInterestOutflow;
                    }
                    outstandingInterestOutflow = 0;
                    break;
                case DEALFEES:
                case UNDRAWN_CAPITAL:
                case SKIMS:
                case CUSTOM_PRE:
                case CUSTOM_EXCEL:
                case INTEREST_AND_DEALFEES:
                case INTEREST_AND_UNDRAWN_CAPITAL:
                case INTEREST_AND_SKIMS:
                case INTEREST_AND_CUSTOM_PRE:
                case INTEREST_AND_CUSTOM_EXCEL:
                case DEALFEES_AND_UNDRAWN_CAPITAL:
                case DEALFEES_AND_SKIMS:
                case DEALFEES_AND_CUSTOM_SPECIFIC:
                case DEALFEES_AND_CUSTOM_PRE:
                case DEALFEES_AND_CUSTOM_EXCEL:
                case DEALFEES_AND_MATURITY:
                case UNDRAWN_CAPITAL_AND_SKIMS:
                case UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC:
                case UNDRAWN_CAPITAL_AND_CUSTOM_PRE:
                case UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL:
                case UNDRAWN_CAPITAL_AND_MATURITY:
                case SKIMS_AND_CUSTOM_SPECIFIC:
                case SKIMS_AND_CUSTOM_PRE:
                case SKIMS_AND_CUSTOM_EXCEL:
                case SKIMS_AND_MATURITY:
                case CUSTOM_PRE_AND_MATURITY:
                case CUSTOM_EXCEL_AND_MATURITY:
                case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL:
                case INTEREST_AND_DEALFEES_AND_SKIMS:
                case INTEREST_AND_DEALFEES_AND_CUSTOM_SPECIFIC:
                case INTEREST_AND_DEALFEES_AND_CUSTOM_PRE:
                case INTEREST_AND_DEALFEES_AND_CUSTOM_EXCEL:
                case INTEREST_AND_DEALFEES_AND_MATURITY:
                case INTEREST_AND_UNDRAWN_CAPITAL_AND_SKIMS:
                case INTEREST_AND_UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC:
                case INTEREST_AND_UNDRAWN_CAPITAL_AND_CUSTOM_PRE:
                case INTEREST_AND_UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL:
                case INTEREST_AND_UNDRAWN_CAPITAL_AND_MATURITY:
                case INTEREST_AND_SKIMS_AND_CUSTOM_SPECIFIC:
                case INTEREST_AND_SKIMS_AND_CUSTOM_PRE:
                case INTEREST_AND_SKIMS_AND_CUSTOM_EXCEL:
                case INTEREST_AND_SKIMS_AND_MATURITY:
                case INTEREST_AND_CUSTOM_PRE_AND_MATURITY:
                case INTEREST_AND_CUSTOM_EXCEL_AND_MATURITY:
                case DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS:
                case DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC:
                case DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_PRE:
                case DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL:
                case DEALFEES_AND_UNDRAWN_CAPITAL_AND_MATURITY:
                case DEALFEES_AND_SKIMS_AND_CUSTOM_SPECIFIC:
                case DEALFEES_AND_SKIMS_AND_CUSTOM_PRE:
                case DEALFEES_AND_SKIMS_AND_CUSTOM_EXCEL:
                case DEALFEES_AND_SKIMS_AND_MATURITY:
                case DEALFEES_AND_CUSTOM_SPECIFIC_AND_MATURITY:
                case DEALFEES_AND_CUSTOM_PRE_AND_MATURITY:
                case DEALFEES_AND_CUSTOM_EXCEL_AND_MATURITY:
                case UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC:
                case UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE:
                case UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL:
                case UNDRAWN_CAPITAL_AND_SKIMS_AND_MATURITY:
                case UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC_AND_MATURITY:
                case UNDRAWN_CAPITAL_AND_CUSTOM_PRE_AND_MATURITY:
                case UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL_AND_MATURITY:
                case SKIMS_AND_CUSTOM_SPECIFIC_AND_MATURITY:
                case SKIMS_AND_CUSTOM_PRE_AND_MATURITY:
                case SKIMS_AND_CUSTOM_EXCEL_AND_MATURITY:
                case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS:
                case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC:
                case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_PRE:
                case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL:
                case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_MATURITY:
                case INTEREST_AND_DEALFEES_AND_SKIMS_AND_CUSTOM_SPECIFIC:
                case INTEREST_AND_DEALFEES_AND_SKIMS_AND_CUSTOM_PRE:
                case INTEREST_AND_DEALFEES_AND_SKIMS_AND_CUSTOM_EXCEL:
                case INTEREST_AND_DEALFEES_AND_SKIMS_AND_MATURITY:
                case INTEREST_AND_DEALFEES_AND_CUSTOM_SPECIFIC_AND_MATURITY:
                case INTEREST_AND_DEALFEES_AND_CUSTOM_PRE_AND_MATURITY:
                case INTEREST_AND_DEALFEES_AND_CUSTOM_EXCEL_AND_MATURITY:
                case INTEREST_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC:
                case INTEREST_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE:
                case INTEREST_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL:
                case INTEREST_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_MATURITY:
                case INTEREST_AND_UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC_AND_MATURITY:
                case INTEREST_AND_UNDRAWN_CAPITAL_AND_CUSTOM_PRE_AND_MATURITY:
                case INTEREST_AND_UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL_AND_MATURITY:
                case INTEREST_AND_SKIMS_AND_CUSTOM_SPECIFIC_AND_MATURITY:
                case INTEREST_AND_SKIMS_AND_CUSTOM_PRE_AND_MATURITY:
                case INTEREST_AND_SKIMS_AND_CUSTOM_EXCEL_AND_MATURITY:
                case DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC:
                case DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE:
                case DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL:
                case DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_MATURITY:
                case DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC_AND_MATURITY:
                case DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_PRE_AND_MATURITY:
                case DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL_AND_MATURITY:
                case DEALFEES_AND_SKIMS_AND_CUSTOM_SPECIFIC_AND_MATURITY:
                case DEALFEES_AND_SKIMS_AND_CUSTOM_PRE_AND_MATURITY:
                case DEALFEES_AND_SKIMS_AND_CUSTOM_EXCEL_AND_MATURITY:
                case UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC_AND_MATURITY:
                case UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE_AND_MATURITY:
                case UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL_AND_MATURITY:
                case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC:
                case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE:
                case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL:
                case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_MATURITY:
                case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC_AND_MATURITY:
                case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_PRE_AND_MATURITY:
                case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL_AND_MATURITY:
                case INTEREST_AND_DEALFEES_AND_SKIMS_AND_CUSTOM_SPECIFIC_AND_MATURITY:
                case INTEREST_AND_DEALFEES_AND_SKIMS_AND_CUSTOM_PRE_AND_MATURITY:
                case INTEREST_AND_DEALFEES_AND_SKIMS_AND_CUSTOM_EXCEL_AND_MATURITY:
                case INTEREST_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC_AND_MATURITY:
                case INTEREST_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE_AND_MATURITY:
                case INTEREST_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL_AND_MATURITY:
                case DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC_AND_MATURITY:
                case DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE_AND_MATURITY:
                case DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL_AND_MATURITY:
                case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC_AND_MATURITY:
                case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE_AND_MATURITY:
                case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL_AND_MATURITY:
                    cashMovement = outstandingInterestOutflow + dealFeesOutflow + interestUndrawnCapitalOutflow + skimsOutflow + customCashflowAmount;
                    if (!isInterestAccrued) {
                        cashMovement += interestOutflow;
                    }
                    if (isCustomAccrued) {
                        cashMovement -= customCashflowAmount;
                    }
                    outstandingInterestOutflow = 0;
                    break;
                default:
                    break;
            }

            // Set Principal outstanding and Cash Movement, and Repayment
            principalAmountVarying -= principalRepayment;
            if (isInterestAccrued) {
                principalAmountVarying += interestOutflow;
            }
            cashflowSchedule.setTotalPrincipalOutstanding(principalAmountVarying);
            if (dateTypeString.contains("MATURITY")) {
                cashMovement += principalAmountVarying;
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

        LocalDate exitDate = generalDetailsInput.get().getExitDate();
        // Calculate Present value sum of Future cashflows after Exit Date
        double presentValueSumExit = cashflow.getSchedules().stream()
                .filter(cashflowSchedule -> cashflowSchedule.getToDate().isAfter(exitDate))
                .mapToDouble(CashflowSchedule::getPresentValue).sum();
        cashflow.setPresentValueSumExit(presentValueSumExit);

        // Calculate % par after Exit Date
        if (principalOutstanding != 0) {
            cashflow.setPercentageParExit(presentValueSumExit * 100 / principalOutstanding);
        }

        // Add irr to Cashflow
        double irr = getInternalRateOfReturn(cashflow, principalOutstanding);
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
                irr += 0.001;
                spv = getSumOfDiscountedPV(schedules, valuationDate, irr);
            }
            return irr;
        } else if (principalAmount > spv) {
            while(principalAmount > spv) {
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

    private void addDealFeesToCashflowSchedule(CashflowSchedule cashflowSchedule,
                                               List<DealFees> dealFees, DayCountConvention dayCountConvention) {

        Optional<DealFees> dealFee = getDealFeesByDate(dealFees, cashflowSchedule.getToDate());
        double annualFeePercentage = getAnnualFeePercentage(dealFee, cashflowSchedule.getToDate());
        double amount = 0.0;
        if (dealFee.isPresent()) {
            FeeBase feeBase = dealFee.get().getFeeBase();
            if (feeBase == FeeBase.COMMITTED_CAPITAL) {
                amount = cashflowSchedule.getCommittedCapital();
            } else if (feeBase == FeeBase.CALL_DOWN_CAPITAL) {
                amount = cashflowSchedule.getCalledDownCapital();
            }
        }
        double dealFeesOutflow = CashflowUtil.getInterestOutflow(
                cashflowSchedule.getFromDate(), cashflowSchedule.getToDate(), dayCountConvention,
                amount, annualFeePercentage);
        cashflowSchedule.setDealFeesOutflow(dealFeesOutflow);
        cashflowSchedule.setAnnualFeePercentage(annualFeePercentage);
    }

    private void addInterestUndrawnCapitalToCashflowSchedule(CashflowSchedule cashflowSchedule,
                                                             List<InterestUndrawnCapital> interestUndrawnCapitals,
                                                             DayCountConvention dayCountConvention) {

        Optional<InterestUndrawnCapital> interestUndrawnCapital = getInterestUndrawnCapitalByDate(
                interestUndrawnCapitals, cashflowSchedule.getToDate());
        double undrawnCapitalPercentage = getInterestUndrawnPercentage(
                interestUndrawnCapital, cashflowSchedule.getToDate());
        double undrawnCapital = cashflowSchedule.getUndrawnCapital();
        double interestUndrawnCapitalOutflow = CashflowUtil.getInterestOutflow(
                cashflowSchedule.getFromDate(), cashflowSchedule.getToDate(), dayCountConvention, undrawnCapital,
                undrawnCapitalPercentage);
        cashflowSchedule.setInterestUndrawnCapitalOutflow(interestUndrawnCapitalOutflow);
        cashflowSchedule.setInterestUndrawnPercentage(undrawnCapitalPercentage);
    }

    private void addSkimsToCashflowSchedule(CashflowSchedule cashflowSchedule, List<Skims> skims,
                                            DayCountConvention dayCountConvention) {
        Optional<Skims> skim = getSkimsByDate(skims, cashflowSchedule.getToDate());
        double skimPercentage = getSkimPercentage(skim, cashflowSchedule.getToDate());
        double amount = 0.0;
        if (skim.isPresent()) {
            amount = skim.get().getOutstandingBalance();
        }
        double skimsOutflow = CashflowUtil.getInterestOutflow(
                cashflowSchedule.getFromDate(), cashflowSchedule.getToDate(), dayCountConvention, amount, skimPercentage);
        cashflowSchedule.setSkimsOutflow(skimsOutflow);
        cashflowSchedule.setSkimPercentage(skimPercentage);
    }

    private void addCustomizableCashflowToCashflowSchedule(CashflowSchedule cashflowSchedule,
                                                           List<Object> customizableCashflowInput,
                                                           DayCountConvention dayCountConvention,
                                                           String dateTypeString) {
        double amount = 0.0;
        Double customCashflowPercentage = null;
        if (dateTypeString.contains("CUSTOM_SPECIFIC") || dateTypeString.contains("CUSTOM_PRE")) {
            List<CustomizableCashflow> customizableCashflows = (List<CustomizableCashflow>)(List<?>) customizableCashflowInput;
            Optional<CustomizableCashflow> customizableCashflow = getCustomPreByDate(customizableCashflows, cashflowSchedule.getToDate());
            if (dateTypeString.contains("CUSTOM_SPECIFIC")) {
                customizableCashflow = getCustomSpecificByDate(customizableCashflows, cashflowSchedule.getToDate());
            }
            if (customizableCashflow.isPresent()) {
                cashflowSchedule.setCustomCashflowName(customizableCashflow.get().getNameOfTheProperty());
                customCashflowPercentage = getCustomCashflowPercentage(customizableCashflow, cashflowSchedule.getToDate());
                amount = customizableCashflow.get().getCashflowFixedAmount();
                if (customizableCashflow.get().getCashflowAmount() == CashflowAmount.PERCENTAGE && customCashflowPercentage != null) {
                    switch (customizableCashflow.get().getCashflowComputationBase()) {
                        case CALL_DOWN_CAPITAL:
                            amount = cashflowSchedule.getCalledDownCapital();
                            break;
                        case COMMITTED_CAPITAL:
                            amount = cashflowSchedule.getCommittedCapital();
                            break;
                        case CUSTOM_AMOUNT:
                            amount = customizableCashflow.get().getCashflowBaseCustomAmount();
                            break;
                        default:
                            break;
                    }
                    switch(customizableCashflow.get().getCashflowDates()) {
                        case SPECIFIC_DATES:

                            amount = CashflowUtil.getInterestOutflow(
                                    cashflowSchedule.getFromDate(), cashflowSchedule.getToDate(), dayCountConvention, amount,
                                    customCashflowPercentage);
                            break;
                        case PRE_EXISTING_DATES:
                            amount = amount * customCashflowPercentage / 100;

                    }
                }
                if (customizableCashflow.get().getCashflowType() == CustomizableCashflowType.INFLOW) {
                    amount *= -1;
                }
            }
            cashflowSchedule.setCustomCashflowPercentage(customCashflowPercentage);
            cashflowSchedule.setCustomCashflowAmount(amount);
        }
        else if (dateTypeString.contains("CUSTOM_EXCEL")) {
            Object customizableCashflowExcels = customizableCashflowInput.stream().findFirst();
            Optional<CustomizableCashflowExcel> customizableCashflowExcel = ((Optional<CustomizableCashflowExcel>) customizableCashflowExcels);
            Optional<InterimPaymentDetails> interimPaymentDetails = getCustomExcelByDate(customizableCashflowExcel, cashflowSchedule.getToDate());
            amount = interimPaymentDetails.map(InterimPaymentDetails::getAmount).orElse(0.0);
            if (customizableCashflowExcel.isPresent()) {
                cashflowSchedule.setCustomCashflowName(customizableCashflowExcel.get().getNameOfTheProperty());
                if (customizableCashflowExcel.get().getCashflowType() == CustomizableCashflowType.INFLOW) {
                    amount *= -1;
                }
            }
            cashflowSchedule.setCustomCashflowAmount(amount);
        }
    }

    private void addPresentValueDetailsToCashflowSchedule(CashflowSchedule cashflowSchedule, DayCountConvention dayCountConvention, double cashMovement) {
        Cashflow cashflow = cashflowSchedule.getCashflow();
        LocalDate date = cashflowSchedule.getToDate();
        LocalDate valuationDate = cashflow.getValuationDate();

        double partialPeriod = CashflowUtil.getPartialPeriod(valuationDate, date, dayCountConvention);
        double discountingFactor = CashflowUtil.getDiscountingFactor(cashflow.getDiscountRate(), partialPeriod);
        double presentValue = CashflowUtil.getPresentValue(cashMovement, discountingFactor, date, valuationDate);

        // Set 0 for all dates before Valuation Date
        if (date.isBefore(valuationDate)) {
            partialPeriod = 0;
            discountingFactor = 0;
        }

        cashflowSchedule.setPartialPeriod(partialPeriod);
        cashflowSchedule.setDiscountingFactor(discountingFactor);
        cashflowSchedule.setPresentValue(presentValue);
    }

    private double getPrepaymentAmountForDate(Optional<RepaymentDetails> input, LocalDate date) {
        double amount = 0;
        if (!input.isPresent()) {
            return amount;
        }
        Set<PaymentSchedule> schedules = input.get().getPaymentSchedules();
        return schedules.stream().filter(schedule -> schedule.getDate().isEqual(date)).findFirst().map(PaymentSchedule::getAmount).orElse(0.0);
    }

    private double getCallPremiumAmountForDate(List<CallPremium> inputs, LocalDate date, double principalRepayment, CashflowSchedule cashflowSchedule) {
        double callPremiumRate = inputs.stream().filter(input -> !input.getDate().isBefore(date))
                .findFirst().map(CallPremium::getPercentage).orElse(0.0);
        double callPremiumAmount = principalRepayment * (callPremiumRate - 100) / 100;
        cashflowSchedule.setCallPremiumRate(callPremiumRate);
        cashflowSchedule.setCallPremiumAmount(callPremiumAmount);
        return callPremiumAmount;
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
        // get single skim details
        // regime start date and end dates are inclusive
        return inputs.stream().filter(input -> !input.getRegimeStartDate().isAfter(date)).
                filter(input -> !input.getRegimeEndDate().isBefore(date)).findFirst();
    }

    private Optional<CustomizableCashflow> getCustomSpecificByDate(List<CustomizableCashflow> inputs, LocalDate date) {
        // get single custom specific details
        // regime start date and end dates are inclusive
        return inputs.stream().filter(input -> !input.getRegimeStartDate().isAfter(date)).
                filter(input -> !input.getRegimeEndDate().isBefore(date)).findFirst();
    }

    private Optional<CustomizableCashflow> getCustomPreByDate(List<CustomizableCashflow> inputs, LocalDate date) {
        // get single custom pre details
        return inputs.stream().filter(input -> input.getDateSelection().isEqual(date)).findFirst();
    }

    private Optional<InterimPaymentDetails> getCustomExcelByDate(Optional<CustomizableCashflowExcel> inputs, LocalDate date) {
        // get single custom excel details
        return inputs.map(customizableCashflowExcel -> customizableCashflowExcel.getInterimPaymentDetails().stream()
                .filter(input -> input.getDate().isEqual(date)).findFirst()).orElse(null);
    }

    private boolean getIsInterestAccrued(List<InterestDetails> input, LocalDate date) {
        Optional<InterestDetails> interestDetail = getInterestDetailsByDate(input , date);
        return interestDetail.map(interestDetails -> interestDetails.getInterestPaidOrAccrued().equals(InterestType.ACCRUED)).orElse(false);
    }

    private boolean getIsCustomAccrued(List<Object> input, LocalDate date, String dateTypeString) {
        if (dateTypeString.contains("CUSTOM_SPECIFIC")) {
            List<CustomizableCashflow> customizableCashflows = (List<CustomizableCashflow>)(List<?>) input;
            Optional<CustomizableCashflow> customizableCashflow = getCustomSpecificByDate(customizableCashflows , date);
            return customizableCashflow.map(element -> element.getCashflowPaymentMode().equals(InterestType.ACCRUED)).orElse(false);
        }
        return false;
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

    private Double getCustomCashflowPercentage(Optional<CustomizableCashflow> input, LocalDate date) {
        if (!input.isPresent()) {
            return null;
        }
        return input.map(CustomizableCashflow::getCashflowPercentage).orElse(null);
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

    private Set<CashflowScheduleDateDto> getCashflowScheduleDates(List<DebtModelInputDto> inputs) {
        Set<CashflowScheduleDateDto> scheduleDates = new HashSet<>();

        // Origination & Maturity dates
        Optional<GeneralDetails> generalDetails = getGeneralDetailsFromInputs(inputs);
        if (!generalDetails.isPresent()) {
            return scheduleDates;
        }
        LocalDate originationDate = generalDetails.get().getOriginationDate();
        scheduleDates.add(new CashflowScheduleDateDto(originationDate, DateType.ORIGINATION));

        LocalDate maturityDate = generalDetails.get().getMaturityDate();

        // Coupon dates
        // Interest Details
        List<InterestDetails> interestDetails = getInterestDetailsFromInputs(inputs);
        AtomicBoolean hasMaturityDateCoupon = new AtomicBoolean(false);
        interestDetails.forEach(interestDetail -> {
            Set<LocalDate> couponDates = interestDetail.getCouponDates();
            scheduleDates.addAll(
                    couponDates.stream()
                            .map(couponDate -> {
                                if (couponDate.isEqual(maturityDate)) {
                                    hasMaturityDateCoupon.set(true);
                                }
                                return new CashflowScheduleDateDto(couponDate, couponDate.isEqual(maturityDate) ? DateType.INTEREST_AND_MATURITY : DateType.INTEREST);
                            })
                            .collect(Collectors.toSet()));
        });

        // Deal Fees
        List<DealFees> dealFees = getDealFeesFromInputs(inputs);
        dealFees.forEach(dealFee -> {
            Set<LocalDate> couponDates = dealFee.getCouponDates();
            couponDates.forEach(couponDate -> {
                AtomicReference<DateType> type = new AtomicReference<>(DateType.DEALFEES);
                if (couponDate.isEqual(maturityDate)) {
                    hasMaturityDateCoupon.set(true);
                    type.set(DateType.DEALFEES_AND_MATURITY);
                }
                // check if this date is already in the schedule
                Set<CashflowScheduleDateDto> existingScheduleDates = scheduleDates.stream()
                        .filter(scheduleDate -> scheduleDate.getDate().isEqual(couponDate)).collect(Collectors.toSet());
                existingScheduleDates.forEach(existingScheduleDate -> {
                    switch (existingScheduleDate.getType()) {
                        case INTEREST:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_DEALFEES);
                            break;
                        case INTEREST_AND_MATURITY:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_DEALFEES_AND_MATURITY);
                            break;
                        default:
                            break;
                    }
                });
                scheduleDates.add(new CashflowScheduleDateDto(couponDate, type.get()));
            });
        });

        // Undrawn Capital
        List<InterestUndrawnCapital> interestUndrawnCapitals = getInterestUndrawnCapitalFromInputs(inputs);
        interestUndrawnCapitals.forEach(interestUndrawnCapital -> {
            Set<LocalDate> couponDates = interestUndrawnCapital.getCouponDates();
            couponDates.forEach(couponDate -> {
                AtomicReference<DateType> type = new AtomicReference<>(DateType.UNDRAWN_CAPITAL);
                if (couponDate.isEqual(maturityDate)) {
                    hasMaturityDateCoupon.set(true);
                    type.set(DateType.UNDRAWN_CAPITAL_AND_MATURITY);
                }
                // check if this date is already in the schedule
                Set<CashflowScheduleDateDto> existingScheduleDates = scheduleDates.stream()
                        .filter(scheduleDate -> scheduleDate.getDate().isEqual(couponDate)).collect(Collectors.toSet());
                existingScheduleDates.forEach(existingScheduleDate -> {
                    switch (existingScheduleDate.getType()) {
                        case INTEREST:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_UNDRAWN_CAPITAL);
                            break;
                        case DEALFEES:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.DEALFEES_AND_UNDRAWN_CAPITAL);
                            break;
                        case INTEREST_AND_DEALFEES:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL);
                            break;
                        case INTEREST_AND_MATURITY:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_UNDRAWN_CAPITAL_AND_MATURITY);
                            break;
                        case DEALFEES_AND_MATURITY:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.DEALFEES_AND_UNDRAWN_CAPITAL_AND_MATURITY);
                            break;
                        case INTEREST_AND_DEALFEES_AND_MATURITY:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_MATURITY);
                            break;
                        default:
                            break;
                    }
                });
                scheduleDates.add(new CashflowScheduleDateDto(couponDate, type.get()));
            });
        });

        // Skims
        List<Skims> skims = getSkimsFromInputs(inputs);
        skims.forEach(skim -> {
            Set<LocalDate> couponDates = skim.getCouponDates();
            couponDates.forEach(couponDate -> {
                AtomicReference<DateType> type = new AtomicReference<>(DateType.SKIMS);
                if (couponDate.isEqual(maturityDate)) {
                    hasMaturityDateCoupon.set(true);
                    type.set(DateType.SKIMS_AND_MATURITY);
                }
                // check if this date is already in the schedule
                Set<CashflowScheduleDateDto> existingScheduleDates = scheduleDates.stream()
                        .filter(scheduleDate -> scheduleDate.getDate().isEqual(couponDate)).collect(Collectors.toSet());
                existingScheduleDates.forEach(existingScheduleDate -> {
                    switch (existingScheduleDate.getType()) {
                        case INTEREST:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_SKIMS);
                            break;
                        case DEALFEES:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.DEALFEES_AND_SKIMS);
                            break;
                        case UNDRAWN_CAPITAL:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.UNDRAWN_CAPITAL_AND_SKIMS);
                            break;
                        case INTEREST_AND_DEALFEES:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_DEALFEES_AND_SKIMS);
                            break;
                        case INTEREST_AND_UNDRAWN_CAPITAL:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_UNDRAWN_CAPITAL_AND_SKIMS);
                            break;
                        case INTEREST_AND_MATURITY:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_SKIMS_AND_MATURITY);
                            break;
                        case DEALFEES_AND_UNDRAWN_CAPITAL:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS);
                            break;
                        case DEALFEES_AND_MATURITY:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.DEALFEES_AND_SKIMS_AND_MATURITY);
                            break;
                        case UNDRAWN_CAPITAL_AND_MATURITY:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.UNDRAWN_CAPITAL_AND_SKIMS_AND_MATURITY);
                            break;
                        case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS);
                            break;
                        case INTEREST_AND_DEALFEES_AND_MATURITY:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_DEALFEES_AND_SKIMS_AND_MATURITY);
                            break;
                        case INTEREST_AND_UNDRAWN_CAPITAL_AND_MATURITY:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_MATURITY);
                        case DEALFEES_AND_UNDRAWN_CAPITAL_AND_MATURITY:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_MATURITY);
                        case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_MATURITY:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_MATURITY);
                            break;
                        default:
                            break;
                    }
                });
                scheduleDates.add(new CashflowScheduleDateDto(couponDate, type.get()));
            });
        });

        // Customizable Cashflow
        List<Object> customizableCashflows = getCustomizableCashflowFromInputs(inputs);
        customizableCashflows.forEach(customizableCashflow -> {
            Set<LocalDate> couponDates = new HashSet<>();

            if (((CustomizableCashflow) customizableCashflow).getCashflowDates() == CashflowDates.SPECIFIC_DATES) {
                couponDates.addAll(((CustomizableCashflow) customizableCashflow).getCouponDates());
                couponDates.forEach(couponDate -> {
                    AtomicReference<DateType> type = new AtomicReference<>(DateType.CUSTOM_SPECIFIC);
                    if (couponDate.isEqual(maturityDate)) {
                        hasMaturityDateCoupon.set(true);
                        type.set(DateType.CUSTOM_SPECIFIC_AND_MATURITY);
                    }
                    // check if this date is already in the schedule
                    Set<CashflowScheduleDateDto> existingScheduleDates = scheduleDates.stream()
                            .filter(scheduleDate -> scheduleDate.getDate().isEqual(couponDate)).collect(Collectors.toSet());
                    existingScheduleDates.forEach(existingScheduleDate -> {
                        switch (existingScheduleDate.getType()) {
                            case INTEREST:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_CUSTOM_SPECIFIC);
                                break;
                            case DEALFEES:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.DEALFEES_AND_CUSTOM_SPECIFIC);
                                break;
                            case UNDRAWN_CAPITAL:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC);
                                break;
                            case SKIMS:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.SKIMS_AND_CUSTOM_SPECIFIC);
                                break;
                            case INTEREST_AND_DEALFEES:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_DEALFEES_AND_CUSTOM_SPECIFIC);
                                break;
                            case INTEREST_AND_UNDRAWN_CAPITAL:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC);
                                break;
                            case INTEREST_AND_SKIMS:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_SKIMS_AND_CUSTOM_SPECIFIC);
                                break;
                            case INTEREST_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_CUSTOM_SPECIFIC_AND_MATURITY);
                                break;
                            case DEALFEES_AND_UNDRAWN_CAPITAL:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC);
                                break;
                            case DEALFEES_AND_SKIMS:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.DEALFEES_AND_SKIMS_AND_CUSTOM_SPECIFIC);
                                break;
                            case DEALFEES_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.DEALFEES_AND_CUSTOM_SPECIFIC_AND_MATURITY);
                                break;
                            case UNDRAWN_CAPITAL_AND_SKIMS:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC);
                                break;
                            case UNDRAWN_CAPITAL_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC_AND_MATURITY);
                                break;
                            case SKIMS_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.SKIMS_AND_CUSTOM_SPECIFIC_AND_MATURITY);
                                break;
                            case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC);
                                break;
                            case INTEREST_AND_DEALFEES_AND_SKIMS:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_DEALFEES_AND_SKIMS_AND_CUSTOM_SPECIFIC);
                                break;
                            case INTEREST_AND_DEALFEES_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_DEALFEES_AND_CUSTOM_SPECIFIC_AND_MATURITY);
                                break;
                            case INTEREST_AND_UNDRAWN_CAPITAL_AND_SKIMS:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC);
                                break;
                            case INTEREST_AND_UNDRAWN_CAPITAL_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC_AND_MATURITY);
                                break;
                            case INTEREST_AND_SKIMS_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_SKIMS_AND_CUSTOM_SPECIFIC_AND_MATURITY);
                                break;
                            case DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC);
                                break;
                            case DEALFEES_AND_UNDRAWN_CAPITAL_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC_AND_MATURITY);
                                break;
                            case DEALFEES_AND_SKIMS_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.DEALFEES_AND_SKIMS_AND_CUSTOM_SPECIFIC_AND_MATURITY);
                                break;
                            case UNDRAWN_CAPITAL_AND_SKIMS_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC_AND_MATURITY);
                                break;
                            case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC);
                                break;
                            case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC_AND_MATURITY);
                                break;
                            case INTEREST_AND_DEALFEES_AND_SKIMS_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_DEALFEES_AND_SKIMS_AND_CUSTOM_SPECIFIC_AND_MATURITY);
                                break;
                            case INTEREST_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC_AND_MATURITY);
                                break;
                            case DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC_AND_MATURITY);
                                break;
                            case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC_AND_MATURITY);
                                break;
                            default:
                                break;
                        }
                    });
                    scheduleDates.add(new CashflowScheduleDateDto(couponDate, type.get()));
                });
            } else if  (((CustomizableCashflow) customizableCashflow).getCashflowDates() == CashflowDates.PRE_EXISTING_DATES) {
                couponDates.add(((CustomizableCashflow) customizableCashflow).getDateSelection());
                couponDates.forEach(couponDate -> {
                    AtomicReference<DateType> type = new AtomicReference<>(DateType.CUSTOM_PRE);
                    if (couponDate.isEqual(maturityDate)) {
                        hasMaturityDateCoupon.set(true);
                        type.set(DateType.CUSTOM_PRE_AND_MATURITY);
                    }
                    // check if this date is already in the schedule
                    Set<CashflowScheduleDateDto> existingScheduleDates = scheduleDates.stream()
                            .filter(scheduleDate -> scheduleDate.getDate().isEqual(couponDate)).collect(Collectors.toSet());
                    existingScheduleDates.forEach(existingScheduleDate -> {
                        switch (existingScheduleDate.getType()) {
                            case INTEREST:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_CUSTOM_PRE);
                                break;
                            case DEALFEES:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.DEALFEES_AND_CUSTOM_PRE);
                                break;
                            case UNDRAWN_CAPITAL:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.UNDRAWN_CAPITAL_AND_CUSTOM_PRE);
                                break;
                            case SKIMS:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.SKIMS_AND_CUSTOM_PRE);
                                break;
                            case INTEREST_AND_DEALFEES:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_DEALFEES_AND_CUSTOM_PRE);
                                break;
                            case INTEREST_AND_UNDRAWN_CAPITAL:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_UNDRAWN_CAPITAL_AND_CUSTOM_PRE);
                                break;
                            case INTEREST_AND_SKIMS:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_SKIMS_AND_CUSTOM_PRE);
                                break;
                            case INTEREST_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_CUSTOM_PRE_AND_MATURITY);
                                break;
                            case DEALFEES_AND_UNDRAWN_CAPITAL:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_PRE);
                                break;
                            case DEALFEES_AND_SKIMS:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.DEALFEES_AND_SKIMS_AND_CUSTOM_PRE);
                                break;
                            case DEALFEES_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.DEALFEES_AND_CUSTOM_PRE_AND_MATURITY);
                                break;
                            case UNDRAWN_CAPITAL_AND_SKIMS:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE);
                                break;
                            case UNDRAWN_CAPITAL_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.UNDRAWN_CAPITAL_AND_CUSTOM_PRE_AND_MATURITY);
                                break;
                            case SKIMS_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.SKIMS_AND_CUSTOM_PRE_AND_MATURITY);
                                break;
                            case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_PRE);
                                break;
                            case INTEREST_AND_DEALFEES_AND_SKIMS:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_DEALFEES_AND_SKIMS_AND_CUSTOM_PRE);
                                break;
                            case INTEREST_AND_DEALFEES_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_DEALFEES_AND_CUSTOM_PRE_AND_MATURITY);
                                break;
                            case INTEREST_AND_UNDRAWN_CAPITAL_AND_SKIMS:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE);
                                break;
                            case INTEREST_AND_UNDRAWN_CAPITAL_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_UNDRAWN_CAPITAL_AND_CUSTOM_PRE_AND_MATURITY);
                                break;
                            case INTEREST_AND_SKIMS_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_SKIMS_AND_CUSTOM_PRE_AND_MATURITY);
                                break;
                            case DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE);
                                break;
                            case DEALFEES_AND_UNDRAWN_CAPITAL_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_PRE_AND_MATURITY);
                                break;
                            case DEALFEES_AND_SKIMS_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.DEALFEES_AND_SKIMS_AND_CUSTOM_PRE_AND_MATURITY);
                                break;
                            case UNDRAWN_CAPITAL_AND_SKIMS_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE_AND_MATURITY);
                                break;
                            case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE);
                                break;
                            case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_PRE_AND_MATURITY);
                                break;
                            case INTEREST_AND_DEALFEES_AND_SKIMS_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_DEALFEES_AND_SKIMS_AND_CUSTOM_PRE_AND_MATURITY);
                                break;
                            case INTEREST_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE_AND_MATURITY);
                                break;
                            case DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE_AND_MATURITY);
                                break;
                            case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE_AND_MATURITY);
                                break;
                            default:
                                break;
                        }
                    });
                    scheduleDates.add(new CashflowScheduleDateDto(couponDate, type.get()));
                });
            } else if  (((CustomizableCashflowExcel) customizableCashflow).getCashflowDates() == CashflowDates.EXCEL_DATES) {
                Set<InterimPaymentDetails> interimPaymentDetails = ((CustomizableCashflowExcel) customizableCashflow).getInterimPaymentDetails();
                interimPaymentDetails.forEach(interimPaymentDetail -> {
                    couponDates.add(interimPaymentDetail.getDate());
                });
                couponDates.forEach(couponDate -> {
                    AtomicReference<DateType> type = new AtomicReference<>(DateType.CUSTOM_EXCEL);
                    if (couponDate.isEqual(maturityDate)) {
                        hasMaturityDateCoupon.set(true);
                        type.set(DateType.CUSTOM_EXCEL_AND_MATURITY);
                    }
                    // check if this date is already in the schedule
                    Set<CashflowScheduleDateDto> existingScheduleDates = scheduleDates.stream()
                            .filter(scheduleDate -> scheduleDate.getDate().isEqual(couponDate)).collect(Collectors.toSet());
                    existingScheduleDates.forEach(existingScheduleDate -> {
                        switch (existingScheduleDate.getType()) {
                            case INTEREST:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_CUSTOM_EXCEL);
                                break;
                            case DEALFEES:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.DEALFEES_AND_CUSTOM_EXCEL);
                                break;
                            case UNDRAWN_CAPITAL:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL);
                                break;
                            case SKIMS:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.SKIMS_AND_CUSTOM_EXCEL);
                                break;
                            case INTEREST_AND_DEALFEES:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_DEALFEES_AND_CUSTOM_EXCEL);
                                break;
                            case INTEREST_AND_UNDRAWN_CAPITAL:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL);
                                break;
                            case INTEREST_AND_SKIMS:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_SKIMS_AND_CUSTOM_EXCEL);
                                break;
                            case INTEREST_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_CUSTOM_EXCEL_AND_MATURITY);
                                break;
                            case DEALFEES_AND_UNDRAWN_CAPITAL:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL);
                                break;
                            case DEALFEES_AND_SKIMS:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.DEALFEES_AND_SKIMS_AND_CUSTOM_EXCEL);
                                break;
                            case DEALFEES_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.DEALFEES_AND_CUSTOM_EXCEL_AND_MATURITY);
                                break;
                            case UNDRAWN_CAPITAL_AND_SKIMS:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL);
                                break;
                            case UNDRAWN_CAPITAL_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL_AND_MATURITY);
                                break;
                            case SKIMS_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.SKIMS_AND_CUSTOM_EXCEL_AND_MATURITY);
                                break;
                            case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL);
                                break;
                            case INTEREST_AND_DEALFEES_AND_SKIMS:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_DEALFEES_AND_SKIMS_AND_CUSTOM_EXCEL);
                                break;
                            case INTEREST_AND_DEALFEES_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_DEALFEES_AND_CUSTOM_EXCEL_AND_MATURITY);
                                break;
                            case INTEREST_AND_UNDRAWN_CAPITAL_AND_SKIMS:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL);
                                break;
                            case INTEREST_AND_UNDRAWN_CAPITAL_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL_AND_MATURITY);
                                break;
                            case INTEREST_AND_SKIMS_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_SKIMS_AND_CUSTOM_EXCEL_AND_MATURITY);
                                break;
                            case DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL);
                                break;
                            case DEALFEES_AND_UNDRAWN_CAPITAL_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL_AND_MATURITY);
                                break;
                            case DEALFEES_AND_SKIMS_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.DEALFEES_AND_SKIMS_AND_CUSTOM_EXCEL_AND_MATURITY);
                                break;
                            case UNDRAWN_CAPITAL_AND_SKIMS_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL_AND_MATURITY);
                                break;
                            case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL);
                                break;
                            case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL_AND_MATURITY);
                                break;
                            case INTEREST_AND_DEALFEES_AND_SKIMS_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_DEALFEES_AND_SKIMS_AND_CUSTOM_EXCEL_AND_MATURITY);
                                break;
                            case INTEREST_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL_AND_MATURITY);
                                break;
                            case DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL_AND_MATURITY);
                                break;
                            case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_MATURITY:
                                scheduleDates.remove(existingScheduleDate);
                                type.set(DateType.INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL_AND_MATURITY);
                                break;
                            default:
                                break;
                        }
                    });
                    scheduleDates.add(new CashflowScheduleDateDto(couponDate, type.get()));
                });
            }
        });

        if (!hasMaturityDateCoupon.get()) {
            scheduleDates.add(new CashflowScheduleDateDto(maturityDate, DateType.MATURITY));
        }

        // Prepayment dates
        Optional<RepaymentDetails> prepaymentDetails = getPrepaymentDetailsFromInputs(inputs);
        if (prepaymentDetails.isPresent()) {
            Set<PaymentSchedule> schedules = prepaymentDetails.get().getPaymentSchedules();
            schedules.forEach(schedule -> {
                LocalDate couponDate = schedule.getDate();
                AtomicReference<DateType> type = new AtomicReference<>(DateType.REPAYMENT);
                // check if this date is already in the schedule
                Set<CashflowScheduleDateDto> existingScheduleDates = scheduleDates.stream()
                        .filter(scheduleDate -> scheduleDate.getDate().isEqual(couponDate)).collect(Collectors.toSet());
                existingScheduleDates.forEach(existingScheduleDate -> {
                    switch (existingScheduleDate.getType()) {
                        case INTEREST:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT);
                            break;
                        case DEALFEES:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_DEALFEES);
                            break;
                        case UNDRAWN_CAPITAL:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_UNDRAWN_CAPITAL);
                            break;
                        case SKIMS:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_SKIMS);
                            break;
                        case CUSTOM_SPECIFIC:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_CUSTOM_SPECIFIC);
                            break;
                        case CUSTOM_PRE:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_CUSTOM_PRE);
                            break;
                        case CUSTOM_EXCEL:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_CUSTOM_EXCEL);
                            break;
                        case INTEREST_AND_DEALFEES:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_DEALFEES);
                            break;
                        case INTEREST_AND_UNDRAWN_CAPITAL:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_UNDRAWN_CAPITAL);
                            break;
                        case INTEREST_AND_SKIMS:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_SKIMS);
                            break;
                        case INTEREST_AND_CUSTOM_SPECIFIC:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_CUSTOM_SPECIFIC);
                            break;
                        case INTEREST_AND_CUSTOM_PRE:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_CUSTOM_PRE);
                            break;
                        case INTEREST_AND_CUSTOM_EXCEL:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_CUSTOM_EXCEL);
                            break;
                        case DEALFEES_AND_UNDRAWN_CAPITAL:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL);
                            break;
                        case DEALFEES_AND_SKIMS:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_DEALFEES_AND_SKIMS);
                            break;
                        case DEALFEES_AND_CUSTOM_SPECIFIC:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_DEALFEES_AND_CUSTOM_SPECIFIC);
                            break;
                        case DEALFEES_AND_CUSTOM_PRE:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_DEALFEES_AND_CUSTOM_PRE);
                            break;
                        case DEALFEES_AND_CUSTOM_EXCEL:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_DEALFEES_AND_CUSTOM_EXCEL);
                            break;
                        case UNDRAWN_CAPITAL_AND_SKIMS:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_UNDRAWN_CAPITAL_AND_SKIMS);
                            break;
                        case UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC);
                            break;
                        case UNDRAWN_CAPITAL_AND_CUSTOM_PRE:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_UNDRAWN_CAPITAL_AND_CUSTOM_PRE);
                            break;
                        case UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL);
                            break;
                        case SKIMS_AND_CUSTOM_SPECIFIC:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_SKIMS_AND_CUSTOM_SPECIFIC);
                            break;
                        case SKIMS_AND_CUSTOM_PRE:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_SKIMS_AND_CUSTOM_PRE);
                            break;
                        case SKIMS_AND_CUSTOM_EXCEL:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_SKIMS_AND_CUSTOM_EXCEL);
                            break;
                        case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL);
                            break;
                        case INTEREST_AND_DEALFEES_AND_SKIMS:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_SKIMS);
                            break;
                        case INTEREST_AND_DEALFEES_AND_CUSTOM_SPECIFIC:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_CUSTOM_SPECIFIC);
                            break;
                        case INTEREST_AND_DEALFEES_AND_CUSTOM_PRE:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_CUSTOM_PRE);
                            break;
                        case INTEREST_AND_DEALFEES_AND_CUSTOM_EXCEL:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_CUSTOM_EXCEL);
                            break;
                        case INTEREST_AND_UNDRAWN_CAPITAL_AND_SKIMS:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_UNDRAWN_CAPITAL_AND_SKIMS);
                            break;
                        case INTEREST_AND_UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC);
                            break;
                        case INTEREST_AND_UNDRAWN_CAPITAL_AND_CUSTOM_PRE:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_UNDRAWN_CAPITAL_AND_CUSTOM_PRE);
                            break;
                        case INTEREST_AND_UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL);
                            break;
                        case INTEREST_AND_SKIMS_AND_CUSTOM_SPECIFIC:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_SKIMS_AND_CUSTOM_SPECIFIC);
                            break;
                        case INTEREST_AND_SKIMS_AND_CUSTOM_PRE:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_SKIMS_AND_CUSTOM_PRE);
                            break;
                        case INTEREST_AND_SKIMS_AND_CUSTOM_EXCEL:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_SKIMS_AND_CUSTOM_EXCEL);
                            break;
                        case DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS);
                            break;
                        case DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC);
                            break;
                        case DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_PRE:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_PRE);
                            break;
                        case DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL);
                            break;
                        case DEALFEES_AND_SKIMS_AND_CUSTOM_SPECIFIC:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_DEALFEES_AND_SKIMS_AND_CUSTOM_SPECIFIC);
                            break;
                        case DEALFEES_AND_SKIMS_AND_CUSTOM_PRE:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_DEALFEES_AND_SKIMS_AND_CUSTOM_PRE);
                            break;
                        case DEALFEES_AND_SKIMS_AND_CUSTOM_EXCEL:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_DEALFEES_AND_SKIMS_AND_CUSTOM_EXCEL);
                            break;
                        case UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC);
                            break;
                        case UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE);
                            break;
                        case UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL);
                            break;
                        case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS);
                            break;
                        case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_SPECIFIC);
                            break;
                        case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_PRE:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_PRE);
                            break;
                        case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_CUSTOM_EXCEL);
                            break;
                        case INTEREST_AND_DEALFEES_AND_SKIMS_AND_CUSTOM_SPECIFIC:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_SKIMS_AND_CUSTOM_SPECIFIC);
                            break;
                        case INTEREST_AND_DEALFEES_AND_SKIMS_AND_CUSTOM_PRE:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_SKIMS_AND_CUSTOM_PRE);
                            break;
                        case INTEREST_AND_DEALFEES_AND_SKIMS_AND_CUSTOM_EXCEL:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_SKIMS_AND_CUSTOM_EXCEL);
                            break;
                        case INTEREST_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC);
                            break;
                        case INTEREST_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE);
                            break;
                        case INTEREST_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL);
                            break;
                        case DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC);
                            break;
                        case DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE);
                            break;
                        case DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL);
                            break;
                        case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_SPECIFIC);
                            break;
                        case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_PRE);
                            break;
                        case INTEREST_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL:
                            scheduleDates.remove(existingScheduleDate);
                            type.set(DateType.INTEREST_AND_REPAYMENT_AND_DEALFEES_AND_UNDRAWN_CAPITAL_AND_SKIMS_AND_CUSTOM_EXCEL);
                            break;
                        default:
                            break;
                    }
                });
                scheduleDates.add(new CashflowScheduleDateDto(couponDate, type.get()));
            });
        }

        // Sort the set chronologically
        return scheduleDates
                .stream()
                .sorted(Comparator.comparing(CashflowScheduleDateDto::getDate))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Optional<GeneralDetails> getGeneralDetailsFromInputs(List<DebtModelInputDto> inputs) {
        return inputs.stream().filter(input -> input.getInputType() == DebtModelInput.GENERAL_DETAILS)
                .map(input -> modelMapper.map(input.getPayload(), GeneralDetails.class)).findFirst();
    }

    private List<InterestDetails> getInterestDetailsFromInputs(List<DebtModelInputDto> inputs) {
        return inputs.stream().filter(input -> input.getInputType() == DebtModelInput.INTEREST_DETAILS)
                .map(input -> Arrays.asList(modelMapper.map(input.getPayload(), InterestDetails[].class)))
                .findFirst().orElse(new ArrayList<InterestDetails>());
    }

    private Optional<RepaymentDetails> getPrepaymentDetailsFromInputs(List<DebtModelInputDto> inputs) {
        return inputs.stream().filter(input -> input.getInputType() == DebtModelInput.REPAYMENT_DETAILS)
                .map(input -> modelMapper.map(input.getPayload(), RepaymentDetails.class)).findFirst();
    }

    private List<DealFees> getDealFeesFromInputs(List<DebtModelInputDto> inputs) {
        return inputs.stream().filter(input -> input.getInputType() == DebtModelInput.DEAL_FEES)
                .map(input -> Arrays.asList(modelMapper.map(input.getPayload(), DealFees[].class)))
                .findFirst().orElse(new ArrayList<DealFees>());
    }

    private List<InterestUndrawnCapital> getInterestUndrawnCapitalFromInputs(List<DebtModelInputDto> inputs) {
        return inputs.stream().filter(input -> input.getInputType() == DebtModelInput.INTEREST_UNDRAWN_CAPITAL)
                .map(input -> Arrays.asList(modelMapper.map(input.getPayload(), InterestUndrawnCapital[].class)))
                .findFirst().orElse(new ArrayList<InterestUndrawnCapital>());
    }

    private List<Skims> getSkimsFromInputs(List<DebtModelInputDto> inputs) {
        return inputs.stream().filter(input -> input.getInputType() == DebtModelInput.SKIMS)
                .map(input -> Arrays.asList(modelMapper.map(input.getPayload(), Skims[].class)))
                .findFirst().orElse(new ArrayList<Skims>());
    }

    private List<CallPremium> getCallPremiumFromInputs(List<DebtModelInputDto> inputs) {
        return inputs.stream().filter(input -> input.getInputType() == DebtModelInput.CALL_PREMIUM)
                .map(input -> Arrays.asList(modelMapper.map(input.getPayload(), CallPremium[].class)))
                .findFirst().orElse(new ArrayList<CallPremium>());
    }

    private List<Object> getCustomizableCashflowFromInputs(List<DebtModelInputDto> inputs) {
        return inputs.stream().filter(input -> input.getInputType() == DebtModelInput.CUSTOMIZABLE_CASHFLOW)
                .map(input -> Arrays.asList(modelMapper.map(input.getPayload(), Object[].class)))
                .findFirst().orElse(new ArrayList<Object>());
    }
}
