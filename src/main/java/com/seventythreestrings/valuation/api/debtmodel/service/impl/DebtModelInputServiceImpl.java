package com.seventythreestrings.valuation.api.debtmodel.service.impl;

import com.seventythreestrings.valuation.api.debtmodel.dto.*;
import com.seventythreestrings.valuation.api.debtmodel.model.*;
import com.seventythreestrings.valuation.api.debtmodel.repository.*;
import com.seventythreestrings.valuation.api.debtmodel.service.DebtModelInputService;
import com.seventythreestrings.valuation.api.debtmodel.service.DebtModelService;
import com.seventythreestrings.valuation.api.exception.AppException;
import com.seventythreestrings.valuation.api.exception.ErrorCodesAndMessages;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class DebtModelInputServiceImpl implements DebtModelInputService {
    private final GeneralDetailsRepository generalDetailsRepository;
    private final InterestDetailsRepository interestDetailsRepository;
    private final PrepaymentDetailsRepository prepaymentDetailsRepository;
    private final DealFeesRepository dealFeesRepository;
    private final InterestUndrwanCapitalRepository interestUndrwanCapitalRepository;
    private final SkimsRepository skimsRepository;
    private final CallPremiumRepository callPremiumRepository;
    private final DiscountRateComputationRepository discountRateComputationRepository;
    private final IssuerFinancialRepository issuerFinancialRepository;
    private final AnnualHistoricalFinancialRepository annualHistoricalFinancialRepository;
    private final AnnualProjectedFinancialRepository annualProjectedFinancialRepository;


    private final DebtModelService debtModelService;
    private final ModelMapper modelMapper;

    @Override
    public List<DebtModelInputDto> getInputsForDebtModel(Long debtModelId) {
        List<DebtModelInputDto> inputs = new ArrayList<>();
        DebtModel debtModel = debtModelService.get(debtModelId);
        // TODO:
        if (debtModel == null || debtModel.getInputs().size() == 0) {
            return inputs;
        }

        for (DebtModelInput input : debtModel.getInputs()) {
            switch (input) {
                case GENERAL_DETAILS:
                    Optional<GeneralDetails> generalDetails = generalDetailsRepository.findFirstByDebtModelId(debtModelId);
                    generalDetails.ifPresent(details -> inputs.add(new DebtModelInputDto(DebtModelInput.GENERAL_DETAILS, modelMapper.map(details, GeneralDetailsDto.class))));
                    break;
                case INTEREST_DETAILS:
                    Optional<InterestDetails> interesetDetailsVersionIdLatest = interestDetailsRepository.findFirstByDebtModelIdOrderByVersionIdDesc(debtModelId);
                    if (interesetDetailsVersionIdLatest.isPresent()) {
                        int versionId = interesetDetailsVersionIdLatest.get().getVersionId();
                        List<InterestDetails> interestDetails = interestDetailsRepository.findAllByDebtModelIdAndVersionId(debtModelId, versionId);
                        inputs.add(new DebtModelInputDto(DebtModelInput.INTEREST_DETAILS, modelMapper.map(interestDetails, InterestDetailsDto[].class)));
                    }
                    break;
                case PREPAYMENT_DETAILS:
                    Optional<PrepaymentDetails> prepaymentDetails = prepaymentDetailsRepository.findFirstByDebtModelId(debtModelId);
                    prepaymentDetails.ifPresent(details -> inputs.add(new DebtModelInputDto(DebtModelInput.PREPAYMENT_DETAILS, modelMapper.map(details, PrepaymentDetailsDto.class))));
                    break;
                case DEAL_FEES:
                    Optional<DealFees> dealFeesVersionIdLatest = dealFeesRepository.findFirstByDebtModelIdOrderByVersionIdDesc(debtModelId);
                    if (dealFeesVersionIdLatest.isPresent()) {
                        int versionId = dealFeesVersionIdLatest.get().getVersionId();
                        List<DealFees> dealFees = dealFeesRepository.findAllByDebtModelIdAndVersionId(debtModelId, versionId);
                        inputs.add(new DebtModelInputDto(DebtModelInput.DEAL_FEES, modelMapper.map(dealFees, DealFees[].class)));
                    }
                    break;
                case INTEREST_UNDRAWN_CAPITAL:
                    Optional<InterestUndrawnCapital> undrawnVersionIdLatest = interestUndrwanCapitalRepository.findFirstByDebtModelIdOrderByVersionIdDesc(debtModelId);
                    if (undrawnVersionIdLatest.isPresent()) {
                        int versionId = undrawnVersionIdLatest.get().getVersionId();
                        List<InterestUndrawnCapital> undrawnCapitals = interestUndrwanCapitalRepository.findAllByDebtModelIdAndVersionId(debtModelId, versionId);
                        inputs.add(new DebtModelInputDto(DebtModelInput.INTEREST_UNDRAWN_CAPITAL, modelMapper.map(undrawnCapitals, InterestUndrawnCapital[].class)));
                    }
                    break;
                case SKIMS:
                    Optional<Skims> skimsVersionIdLatest = skimsRepository.findFirstByDebtModelIdOrderByVersionIdDesc(debtModelId);
                    if (skimsVersionIdLatest.isPresent()) {
                        int versionId = skimsVersionIdLatest.get().getVersionId();
                        List<Skims> skims = skimsRepository.findAllByDebtModelIdAndVersionId(debtModelId, versionId);
                        inputs.add(new DebtModelInputDto(DebtModelInput.SKIMS, modelMapper.map(skims, Skims[].class)));
                    }
                    break;
                default:
                    break;
            }
        }

        return inputs;
    }

    @SneakyThrows
    @Override
    public Object get(DebtModelInput inputType, Long id) {
        switch (inputType) {
            case GENERAL_DETAILS:
                GeneralDetails generalDetails = generalDetailsRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCodesAndMessages.NOT_FOUND_EXCEPTION));
                return modelMapper.map(generalDetails, GeneralDetailsDto.class);
            case INTEREST_DETAILS:
                InterestDetails interestDetails = interestDetailsRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCodesAndMessages.NOT_FOUND_EXCEPTION));
                return modelMapper.map(interestDetails, InterestDetailsDto.class);
            case PREPAYMENT_DETAILS:
                PrepaymentDetails prepaymentDetails = prepaymentDetailsRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCodesAndMessages.NOT_FOUND_EXCEPTION));
                return modelMapper.map(prepaymentDetails, PrepaymentDetailsDto.class);
            case DEAL_FEES:
                DealFees dealFees = dealFeesRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCodesAndMessages.NOT_FOUND_EXCEPTION));
                return modelMapper.map(dealFees,DealFeesDto.class);
            case INTEREST_UNDRAWN_CAPITAL:
                InterestUndrawnCapital interestUndrawnCapital = interestUndrwanCapitalRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCodesAndMessages.NOT_FOUND_EXCEPTION));
                return modelMapper.map(interestUndrawnCapital,InterestUndrawnCapitalDto.class);
            case SKIMS:
                Skims skims = skimsRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCodesAndMessages.NOT_FOUND_EXCEPTION));
                return modelMapper.map(skims,SkimsDto.class);
            case CALL_PREMIUM:
                CallPremium callPremium = callPremiumRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCodesAndMessages.NOT_FOUND_EXCEPTION));
                return modelMapper.map(callPremium,CallPremiumDto.class);
            default:
                return null;
        }
    }

    @Transactional
    @Override
    public Object create(DebtModelInput inputType, Object o, Long debtModelId) {
        DebtModel debtModel = debtModelService.get(debtModelId);
        switch (inputType) {
            case GENERAL_DETAILS:
                GeneralDetails generalDetails = modelMapper.map(o, GeneralDetails.class);
                generalDetails.setDebtModel(debtModel);
                debtModel.setGeneralDetails(generalDetails);
                debtModelService.save(debtModel);
                return modelMapper.map(generalDetailsRepository.save(generalDetails), GeneralDetailsDto.class);
            case INTEREST_DETAILS:
                List<InterestDetails> interestDetails = Arrays.asList(modelMapper.map(o, InterestDetails[].class));
                for(InterestDetails interestDetail: interestDetails){
                    interestDetail.setDebtModel(debtModel);
                }
                return Arrays.asList(modelMapper.map(interestDetailsRepository.saveAll(interestDetails), InterestDetailsDto[].class));
            case PREPAYMENT_DETAILS:
                PrepaymentDetails prepaymentDetails = modelMapper.map(o, PrepaymentDetails.class);
                prepaymentDetails.setDebtModel(debtModel);
                prepaymentDetails.getPaymentSchedules().forEach(paymentSchedule -> paymentSchedule.setPrepaymentDetails(prepaymentDetails));
                return modelMapper.map(prepaymentDetailsRepository.save(prepaymentDetails), PrepaymentDetailsDto.class);
            case DEAL_FEES:
                List<DealFees> dealFees = Arrays.asList(modelMapper.map(o,DealFees[].class));
                for(DealFees dealFee: dealFees){
                    dealFee.setDebtModel(debtModel);
                }
                return Arrays.asList(modelMapper.map(dealFeesRepository.saveAll(dealFees),DealFeesDto[].class));
            case INTEREST_UNDRAWN_CAPITAL:
                List<InterestUndrawnCapital> interestUndrawnCapital = Arrays.asList(modelMapper.map(o,InterestUndrawnCapital[].class));
                for(InterestUndrawnCapital interestUndrawn:interestUndrawnCapital) {
                    interestUndrawn.setDebtModel(debtModel);
                }
                return Arrays.asList(modelMapper.map(interestUndrwanCapitalRepository.saveAll(interestUndrawnCapital),InterestUndrawnCapitalDto[].class));
            case SKIMS:
                List<Skims> skims = Arrays.asList(modelMapper.map(o,Skims[].class));
                for(Skims ski:skims) {
                    ski.setDebtModel(debtModel);
                }
                return Arrays.asList(modelMapper.map(skimsRepository.saveAll(skims),SkimsDto[].class));
            case CALL_PREMIUM:
                List<CallPremium> callPremium = Arrays.asList(modelMapper.map(o,CallPremium[].class));
                for(CallPremium call:callPremium) {
                    call.setDebtModel(debtModel);
                }
                return Arrays.asList(modelMapper.map(callPremiumRepository.saveAll(callPremium),CallPremiumDto[].class));
            case ISSUER_FINANCIAL:
                IssuerFinancial issuerFinancial = modelMapper.map(o,IssuerFinancial.class);
                issuerFinancial.setDebtModel(debtModel);
                AnnualHistoricalFinancial annualHistoricalFinancial =   issuerFinancial.getAnnualHistoricalFinancial();
                issuerFinancial.setAnnualHistoricalFinancial(annualHistoricalFinancial);
                AnnualProjectedFinancial annualProjectedFinancial = issuerFinancial.getAnnualProjectedFinancial();
                issuerFinancial.setAnnualProjectedFinancial(annualProjectedFinancial);
                modelMapper.map(annualHistoricalFinancialRepository.save(annualHistoricalFinancial),AnnualHistoricalFinancialDto.class);
                modelMapper.map(annualProjectedFinancialRepository.save(annualProjectedFinancial),AnnualProjectedFinancialDto.class);
                  return modelMapper.map(issuerFinancialRepository.save(issuerFinancial),IssuerFinancialDto.class);
            default:
                break;
        }
        return null;
    }

    @Override
    public Object createDiscount(DiscountRateComputationDto discountRateComputationDto){
        DiscountRateComputaion discountRateComputaion = modelMapper.map(discountRateComputationDto, DiscountRateComputaion.class);
        discountRateComputaion.getDiscountAdjustments().forEach(discountAdjustment -> discountAdjustment.setDiscountRateComputation(discountRateComputaion));
        return modelMapper.map(discountRateComputationRepository.save(discountRateComputaion), DiscountRateComputationDto.class);
    }

    @Override
    public Object update(DebtModelInput inputType, Object o, Long debtModelId) {
        return create(inputType, o, debtModelId);
    }

    @Transactional
    @Override
    public void delete(DebtModelInput inputType, Long id, Long debtModelId) {
        DebtModel debtModel = debtModelService.get(debtModelId);
        switch (inputType) {
            case GENERAL_DETAILS:
                generalDetailsRepository.deleteById(id);
                debtModel.setGeneralDetails(null);
                break;
            case INTEREST_DETAILS:
                interestDetailsRepository.deleteById(id);
                break;
            case PREPAYMENT_DETAILS:
                prepaymentDetailsRepository.deleteById(id);
                break;
            default:
                break;
        }

        List<DebtModelInput> debtModelInputs = debtModel.getInputs();
        while (debtModelInputs.contains(inputType)) {
            debtModelInputs.remove(inputType);
        }
        debtModel.setInputs(debtModelInputs);
        debtModelService.save(debtModel);
    }
}
