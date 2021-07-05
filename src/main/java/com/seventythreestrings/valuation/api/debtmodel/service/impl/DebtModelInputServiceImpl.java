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
                    Optional<InterestDetails> interestDetails = interestDetailsRepository.findFirstByDebtModelId(debtModelId);
                    interestDetails.ifPresent(details -> inputs.add(new DebtModelInputDto(DebtModelInput.INTEREST_DETAILS, modelMapper.map(details, InterestDetailsDto.class))));
                    break;
                case PREPAYMENT_DETAILS:
                    Optional<PrepaymentDetails> prepaymentDetails = prepaymentDetailsRepository.findFirstByDebtModelId(debtModelId);
                    prepaymentDetails.ifPresent(details -> inputs.add(new DebtModelInputDto(DebtModelInput.PREPAYMENT_DETAILS, modelMapper.map(details, PrepaymentDetailsDto.class))));
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
                InterestDetails interestDetails = modelMapper.map(o, InterestDetails.class);
                interestDetails.setDebtModel(debtModel);
                return modelMapper.map(interestDetailsRepository.save(interestDetails), InterestDetailsDto.class);
            case PREPAYMENT_DETAILS:
                PrepaymentDetails prepaymentDetails = modelMapper.map(o, PrepaymentDetails.class);
                prepaymentDetails.setDebtModel(debtModel);
                prepaymentDetails.getPaymentSchedules().forEach(paymentSchedule -> paymentSchedule.setPrepaymentDetails(prepaymentDetails));
                return modelMapper.map(prepaymentDetailsRepository.save(prepaymentDetails), PrepaymentDetailsDto.class);
            case DEAL_FEES:
                DealFees dealFees = modelMapper.map(o,DealFees.class);
                dealFees.setDebtModel(debtModel);
                DealFees savedDealFees = dealFeesRepository.save(dealFees);
                return modelMapper.map(savedDealFees,DealFeesDto.class);
            case INTEREST_UNDRAWN_CAPITAL:
                InterestUndrawnCapital interestUndrawnCapital = modelMapper.map(o,InterestUndrawnCapital.class);
                interestUndrawnCapital.setDebtModel(debtModel);
                InterestUndrawnCapital savedInterestCapital = interestUndrwanCapitalRepository.save(interestUndrawnCapital);
                return modelMapper.map(savedInterestCapital,InterestUndrawnCapitalDto.class);
            case SKIMS:
                Skims skims = modelMapper.map(o,Skims.class);
                skims.setDebtModel(debtModel);
                Skims savedSkim = skimsRepository.save(skims);
                return modelMapper.map(savedSkim,SkimsDto.class);
            case CALL_PREMIUM:
                CallPremium callPremium = modelMapper.map(o,CallPremium.class);
                callPremium.setDebtModel(debtModel);
                CallPremium savedCallPremium = callPremiumRepository.save(callPremium);
                return modelMapper.map(savedCallPremium,CallPremiumDto.class);
            default:
                break;
        }
        return null;
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
