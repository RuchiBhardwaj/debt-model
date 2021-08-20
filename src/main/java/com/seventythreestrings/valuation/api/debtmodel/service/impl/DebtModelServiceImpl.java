package com.seventythreestrings.valuation.api.debtmodel.service.impl;

import com.seventythreestrings.valuation.api.debtmodel.dto.*;
import com.seventythreestrings.valuation.api.debtmodel.model.DebtModel;
import com.seventythreestrings.valuation.api.debtmodel.model.GeneralDetails;
import com.seventythreestrings.valuation.api.debtmodel.model.LookUpDebtDetails;
import com.seventythreestrings.valuation.api.debtmodel.model.LookUpValuationDetails;
import com.seventythreestrings.valuation.api.debtmodel.repository.DebtModelRepository;
import com.seventythreestrings.valuation.api.debtmodel.repository.GeneralDetailsRepository;
import com.seventythreestrings.valuation.api.debtmodel.repository.LookUpDebtDetailsRepository;
import com.seventythreestrings.valuation.api.debtmodel.repository.LookUpValuationDetailsRepository;
import com.seventythreestrings.valuation.api.debtmodel.service.DebtModelService;
import com.seventythreestrings.valuation.api.exception.AppException;
import com.seventythreestrings.valuation.api.exception.ErrorCodesAndMessages;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class DebtModelServiceImpl implements DebtModelService {
    private final DebtModelRepository repository;
    private final ModelMapper modelMapper;
    private final LookUpDebtDetailsRepository lookUpDebtDetailsRepository;
    private final LookUpValuationDetailsRepository lookUpValuationDetailsRepository;
    private final GeneralDetailsRepository generalDetailsRepository;

    @Override
    public List<DebtModel> getAll() {
        return repository.findAll();
    }

    @Override
    public Page<DebtModel> getAllPaginatedWithGeneralDetailsAndCashflow(String sortField, SortOrder sortOrder, int pageNumber, int pageSize) {
        Sort sort = sortOrder.equals(SortOrder.ASC) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        return repository.findAll(PageRequest.of(pageNumber, pageSize, sort));
    }

    @SneakyThrows
    @Override
    public DebtModel get(Long id) {
        return repository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCodesAndMessages.NOT_FOUND_EXCEPTION));
    }

    @SneakyThrows
    @Override
    public List<DebtModelDto> getListOfDebtModels(Long fundId){
        List<DebtModelDto> inp = new ArrayList<>();
        List<DebtModel> debtModels = repository.findAllByFundId(fundId);
        modelMapper.map(debtModels,DebtModelDto[].class);
        for(DebtModel d : debtModels){
            DebtModelDto map = modelMapper.map(d, DebtModelDto.class);
            inp.add(map);
        }
        return inp;
    }

    @Override
    public DebtModel create(DebtModel model) {
        return repository.save(model);
    }

    @Override
    public DebtModel update(DebtModel model) {
        return repository.save(model);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void save(DebtModel model) {
        repository.save(model);
    }

    //funds and company function

    public LookUpDebtDetails saveLookUpDebtDetail(LookUpDebtDetails model) {return lookUpDebtDetailsRepository.save(model);}

    public LookUpValuationDetails saveLookUpValuationDetails(LookUpValuationDetails lookUpValuationDetails){return lookUpValuationDetailsRepository.save(lookUpValuationDetails);}

    @SneakyThrows
    @Override
    public CompanyDetailsDto getCompany(UUID companyId)  {
        CompanyDetailsDto companyDetails = new CompanyDetailsDto();
        Optional<LookUpDebtDetails> lookUpDebtDetails = lookUpDebtDetailsRepository.findDebtIdByCompanyId(companyId);
        if(lookUpDebtDetails.isPresent()){
            Long debtId = lookUpDebtDetails.get().getDebtId();
            Optional<GeneralDetails> generalDetails = generalDetailsRepository.findFirstByDebtModelId(debtId);
            companyDetails.setCompanyName(lookUpDebtDetails.get().getCompanyName());
            companyDetails.setCompanyId(lookUpDebtDetails.get().getCompanyId());
            companyDetails.setCompanyDetails(modelMapper.map(generalDetails,GeneralDetailsDto.class));
        }
        return companyDetails;
    }



}
