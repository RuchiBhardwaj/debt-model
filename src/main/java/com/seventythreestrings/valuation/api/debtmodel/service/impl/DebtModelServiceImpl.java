package com.seventythreestrings.valuation.api.debtmodel.service.impl;

import com.seventythreestrings.valuation.api.debtmodel.dto.*;
import com.seventythreestrings.valuation.api.debtmodel.enums.SortOrder;
import com.seventythreestrings.valuation.api.debtmodel.model.*;
import com.seventythreestrings.valuation.api.debtmodel.repository.*;
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
import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
@Transactional
public class DebtModelServiceImpl implements DebtModelService {
    private final DebtModelRepository repository;
    private final ModelMapper modelMapper;
    private final LookUpDebtDetailsRepository lookUpDebtDetailsRepository;
    private final LookUpValuationDetailsRepository lookUpValuationDetailsRepository;
    private final GeneralDetailsRepository generalDetailsRepository;
    private final CashflowRepository cashflowRepository;
    private final IssuerFinancialRepository issuerFinancialRepository;
    private final AnnualHistoricalFinancialRepository annualHistoricalFinancialRepository;

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
        CompanyDetailsDto companyDetailsData = new CompanyDetailsDto();
        Optional<LookUpDebtDetails> lookUpDebtDetails = lookUpDebtDetailsRepository.findDebtIdByCompanyId(companyId);
        if(lookUpDebtDetails.isPresent()){
            Long debtId = lookUpDebtDetails.get().getDebtId();
            GeneralDetails generalDetails = generalDetailsRepository.findFirstByDebtModelIdCompanyDetails(debtId);
            companyDetailsData.setCompanyName(lookUpDebtDetails.get().getCompanyName());
            companyDetailsData.setCompanyId(lookUpDebtDetails.get().getCompanyId());
            companyDetailsData.setCompanyDetails(modelMapper.map(generalDetails,GeneralDetailsDto.class));
        }
        return companyDetailsData;
    }

    @SneakyThrows
    @Override
    public FundDetailsResponseDto getFundDetails(FundDetailsDto fundDetailsDto) {
        FundDetailsResponseDto fundDetailsResponseDto = new FundDetailsResponseDto();
        UUID fund = fundDetailsDto.getFundId();
        List<CompanyResponseDto> companyResponse = new ArrayList<>();

        for(CompanyDto f : fundDetailsDto.getCompanies()){
            UUID companyId = f.getCompanyId();
            Optional<LookUpDebtDetails> lookUpDebtDetails = lookUpDebtDetailsRepository.findDebtIdByCompanyId(companyId);
            Long debtId = lookUpDebtDetails.get().getDebtId();
            List<ValuationResponseDto> valuationResponses = new ArrayList<>();
            for(ValuationDatesDto v:f.getValuationDates()){
                UUID valuationDateId = v.getValuationDateId();
                Optional<LookUpValuationDetails> valuationDate = lookUpValuationDetailsRepository.findValuationDateByValuationDateId(valuationDateId);
                LocalDate valDate = valuationDate.get().getValuationDate();
                Integer version = valuationDate.get().getVersionId();
                if(debtId !=null ){
                    CompanyResponseDto companyResponse1 = new CompanyResponseDto();
                    GeneralDetails generalDetails = generalDetailsRepository.findFirstByDebtModelIdAndValuationDate(debtId);
                    fundDetailsResponseDto.setFundId(fund);
                    ValuationResponseDto val = new ValuationResponseDto();
                    if(generalDetails!=null) {
                        val.setValuationDates(modelMapper.map(generalDetails, GeneralDetailsDto.class));
                    }
                    valuationResponses.add(val);
                    companyResponse1.setCompanyId(companyId);
                    companyResponse1.setValuationDates(valuationResponses);
                    companyResponse.add(companyResponse1);
                }
            }
            fundDetailsResponseDto.setCompanies(companyResponse);
        }
        return fundDetailsResponseDto;

    }



    @SneakyThrows
    @Override
    public FundValuationCompanyResponseDto getValuationDetails(FundDetailsDto fundDetailsDto){
        FundValuationCompanyResponseDto fundDetailsResponseDto = new FundValuationCompanyResponseDto();
        UUID fund = fundDetailsDto.getFundId();
        List<CompanyValuationResponseDto> companyResponse = new ArrayList<>();
        for(CompanyDto f : fundDetailsDto.getCompanies()) {
            UUID companyId = f.getCompanyId();
            Optional<LookUpDebtDetails> lookUpDebtDetails = lookUpDebtDetailsRepository.findDebtIdByCompanyId(companyId);
            Long debtId = lookUpDebtDetails.get().getDebtId();
            List<ValuationDetailsDto> valuationResponses = new ArrayList<>();
            for (ValuationDatesDto v : f.getValuationDates()) {
                UUID valuationDateId = v.getValuationDateId();
                Optional<LookUpValuationDetails> valuationDate = lookUpValuationDetailsRepository.findValuationDateByValuationDateId(valuationDateId);
                String valDate = String.valueOf(valuationDate.get().getValuationDate());
                Integer version = valuationDate.get().getVersionId();
                if (debtId != null) {
                    CompanyValuationResponseDto companyResponse1 = new CompanyValuationResponseDto();
                    List<Cashflow> cashflow = cashflowRepository.findAllByDebtModelIdAndVersionId(debtId,version);
                    fundDetailsResponseDto.setFundId(fund);
                    ValuationDetailsDto val = new ValuationDetailsDto();
                    if(cashflow != null && cashflow.size()>0){
                        val.setValue(cashflow.get(0).getPresentValueSum());
                        val.setYtm(cashflow.get(0).getInternalRateOfReturn());
                    }
                    val.setValuationDateID(v.getValuationDateId());
                    IssuerFinancial issuerFinancial = issuerFinancialRepository.findByDebtModelIdAndVersionId(debtId, version);
                    if(issuerFinancial!=null) {
                        Long issuerId = issuerFinancial.getId();
                        AnnualHistoricalFinancial annualHistoricalFinancial = annualHistoricalFinancialRepository.findByIssuerFinancialAndYear(issuerId, "LTM");
                        if (annualHistoricalFinancial != null) {
                            val.setCash(annualHistoricalFinancial.getCash());
                            val.setEbitda(annualHistoricalFinancial.getEbitda());
                            val.setRevenue(annualHistoricalFinancial.getTotalRevenue());
                        }
                    }
                    val.setDate(valDate);
                    valuationResponses.add(val);
                    companyResponse1.setCompanyId(companyId);
                    companyResponse1.setValuationDates(valuationResponses);
                    companyResponse.add(companyResponse1);
                }
            }
            fundDetailsResponseDto.setCompanies(companyResponse);
        }
        return fundDetailsResponseDto;
    }





    @SneakyThrows
    @Override
    public LookUpValuationDetails updateLookUpValuationDetails(LookUpValuationDetails lookUpValuationDetails){return lookUpValuationDetailsRepository.save(lookUpValuationDetails);}

}
