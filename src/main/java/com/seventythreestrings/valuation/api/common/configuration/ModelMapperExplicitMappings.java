package com.seventythreestrings.valuation.api.common.configuration;

import com.seventythreestrings.valuation.api.debtmodel.dto.*;
import com.seventythreestrings.valuation.api.debtmodel.model.*;
import org.modelmapper.*;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class ModelMapperExplicitMappings {

    EntityManager em;

    @Autowired
    ModelMapperExplicitMappings(EntityManager em) {
        this.em = em;
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper
                .getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                // handle lazily loaded fields
                .setPropertyCondition(context -> em.getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(context.getSource()))
                .setPropertyCondition(Conditions.isNotNull());

        // Debt Model
        modelMapper.addMappings(mapDebtModelDtoToModel());

        // General Details
        modelMapper.addMappings(mapGeneralDetailsDtoToModel());
        modelMapper.addMappings(mapGeneralDetailsModelToDto());

        // Interest Details
        modelMapper.addMappings(mapInterestDetailsDtoToModel());
        modelMapper.addMappings(mapInterestDetailsModelToDto());

        // Prepayment Details
        modelMapper.addMappings(mapPrepaymentDetailsDtoToModel());
        modelMapper.addMappings(mapPrepaymentDetailsModelToDto());

        // Payment Schedule
        modelMapper.addMappings(mapPaymentScheduleDtoToModel());
        modelMapper.addMappings(mapPaymentScheduleModelToDto());

        // Base Rate Curve
        modelMapper.addMappings(mapBaseRateCurveDtoToModel());
        modelMapper.addMappings(mapBaseRateCurveModelToDto());

        //Deal Fees
        modelMapper.addMappings((mapDealFeesDtoToModel()));
        modelMapper.addMappings(mapDealFeesModelToDto());

        //InterestUndrawnCapital
        modelMapper.addMappings((mapInterestUndrawnDtoToModel()));
        modelMapper.addMappings((mapInterestUndrawnModeltoDto()));

        //Skims
        modelMapper.addMappings((mapSkimsDtoToModel()));
        modelMapper.addMappings((mapSkimsModelToDto()));

        //Call Premium
        modelMapper.addMappings((mapCallPremiumDtoToModel()));
        modelMapper.addMappings((mapCallPremiumModelToDto()));

        //Issuer Financial
        modelMapper.addMappings((mapIssuerFinancialDtoToModel()));
        modelMapper.addMappings((mapIssuerFinancialModelToDto()));

        //Annual Projected Financial
        modelMapper.addMappings((mapAnnualProjectedFinancialDtoTOModel()));
        modelMapper.addMappings((mapAnnualProjectedFinancialModelToDto()));

        //Annual Historical Financial
        modelMapper.addMappings((mapAnnualHistoricalFinancialDtoTOModel()));
        modelMapper.addMappings((mapAnnualHistoricalFinancialModelToDto()));

        //CustomizableCashflow
        modelMapper.addMappings((mapCustomizableCashflowDtoToModel()));
        modelMapper.addMappings((mapCustomizableCashflowModelToDto()));

        //CustomizableCashflowExcel
        modelMapper.addMappings((mapCustomizableCashflowExcelDtoToModel()));
        modelMapper.addMappings((mapCustomizableCashflowExcelModelToDto()));

        //InterimPaymentDetails
        modelMapper.addMappings((mapInterimPaymentDetailsDtoToModel()));
        modelMapper.addMappings((mapInterimPaymentDetailsModelToDto()));


        //DiscountRate
        modelMapper.addMappings((mapDiscountRateComputationModelToDto()));
        modelMapper.addMappings((mapDiscountRateComputationDtoToModel()));

        //DiscountAdjustment
        modelMapper.addMappings((mapDiscountAdjustmentModelToDto()));
        modelMapper.addMappings((mapDiscountAdjustmentDtoToModel()));

        //LookUpDebtDetails
        modelMapper.addMappings((mapLookUPDebtDetailsDtoToModel()));

        //LookUPValuationDetails
        modelMapper.addMappings((mapLookUpValuationDetailsDtoToModel()));
        modelMapper.addMappings((mapLookUpValuationDetailsModelToDto()));

        // LocalDate
        modelMapper.createTypeMap(String.class, LocalDate.class);
        modelMapper.addConverter(toStringDate);
        modelMapper.getTypeMap(String.class, LocalDate.class).setProvider(localDateProvider);

        // LocalDateTime
        modelMapper.createTypeMap(String.class, LocalDateTime.class);
        modelMapper.addConverter(toStringDateTime);
        modelMapper.getTypeMap(String.class, LocalDateTime.class).setProvider(localDateTimeProvider);

        return modelMapper;
    }

    // Converters
    Provider<LocalDate> localDateProvider = new AbstractProvider<LocalDate>() {
        @Override
        public LocalDate get() {
            return LocalDate.now();
        }
    };

    Converter<String, LocalDate> toStringDate = new AbstractConverter<String, LocalDate>() {
        @Override
        protected LocalDate convert(String source) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(source, format);
        }
    };

    Provider<LocalDateTime> localDateTimeProvider = new AbstractProvider<LocalDateTime>() {
        @Override
        public LocalDateTime get() {
            return LocalDateTime.now();
        }
    };

    Converter<String, LocalDateTime> toStringDateTime = new AbstractConverter<String, LocalDateTime>() {
        @Override
        protected LocalDateTime convert(String source) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            return LocalDateTime.parse(source, format);
        }
    };


    // Debt Model
    PropertyMap<DebtModelDto, DebtModel> mapDebtModelDtoToModel() {
        return new PropertyMap<DebtModelDto, DebtModel>() {
            @Override
            protected void configure() {
                map().setInputs(source.getInputs());
            }
        };
    }

    // General Details
    PropertyMap<GeneralDetailsDto, GeneralDetails> mapGeneralDetailsDtoToModel() {
        return new PropertyMap<GeneralDetailsDto, GeneralDetails>() {
            @Override
            protected void configure() {
                map().getDebtModel().setId(source.getDebtModelId());
            }
        };
    }

    PropertyMap<GeneralDetails, GeneralDetailsDto> mapGeneralDetailsModelToDto () {
        return new PropertyMap<GeneralDetails, GeneralDetailsDto>() {
            @Override
            protected void configure() {
                map().setDebtModelId(source.getDebtModel().getId());
            }
        };
    }

    // Interest Details
    PropertyMap<InterestDetailsDto, InterestDetails> mapInterestDetailsDtoToModel() {
        return new PropertyMap<InterestDetailsDto, InterestDetails>() {
            @Override
            protected void configure() {
                map().getDebtModel().setId(source.getDebtModelId());
            }
        };
    }

    PropertyMap<InterestDetails, InterestDetailsDto> mapInterestDetailsModelToDto () {
        return new PropertyMap<InterestDetails, InterestDetailsDto>() {
            @Override
            protected void configure() {
                map().setDebtModelId(source.getDebtModel().getId());
            }
        };
    }

    // Prepayment Details
    PropertyMap<PrepaymentDetailsDto, PrepaymentDetails> mapPrepaymentDetailsDtoToModel() {
        return new PropertyMap<PrepaymentDetailsDto, PrepaymentDetails>() {
            @Override
            protected void configure() {
                map().getDebtModel().setId(source.getDebtModelId());
            }
        };
    }

    PropertyMap<PrepaymentDetails, PrepaymentDetailsDto> mapPrepaymentDetailsModelToDto() {
        return new PropertyMap<PrepaymentDetails, PrepaymentDetailsDto>() {
            @Override
            protected void configure() {
                map().setDebtModelId(source.getDebtModel().getId());
            }
        };
    }

    // Payment Schedule
    PropertyMap<PaymentScheduleDto, PaymentSchedule> mapPaymentScheduleDtoToModel() {
        return new PropertyMap<PaymentScheduleDto, PaymentSchedule>() {
            @Override
            protected void configure() {
                map().getPrepaymentDetails().setId(source.getPrepaymentDetailsId());
            }
        };
    }

    PropertyMap<PaymentSchedule, PaymentScheduleDto> mapPaymentScheduleModelToDto() {
        return new PropertyMap<PaymentSchedule, PaymentScheduleDto>() {
            @Override
            protected void configure() {
                map().setPrepaymentDetailsId(source.getPrepaymentDetails().getId());
            }
        };
    }

    // Base Rate Curve
    PropertyMap<BaseRateCurveDto, BaseRateCurve> mapBaseRateCurveDtoToModel() {
        return new PropertyMap<BaseRateCurveDto, BaseRateCurve>() {
            @Override
            protected void configure() {
                map().getBaseRate().setId(source.getBaseRateId());
            }
        };
    }

    PropertyMap<BaseRateCurve, BaseRateCurveDto> mapBaseRateCurveModelToDto() {
        return new PropertyMap<BaseRateCurve, BaseRateCurveDto>() {
            @Override
            protected void configure() {
                map().setBaseRateId(source.getBaseRate().getId());
            }
        };
    }

    //Deal Fees
    PropertyMap<DealFeesDto,DealFees> mapDealFeesDtoToModel(){
        return  new PropertyMap<DealFeesDto, DealFees>() {
            @Override
            protected void configure() {
                map().getDebtModel().setId(source.getDebtModelId());
            }
        };
    }

    PropertyMap<DealFees, DealFeesDto> mapDealFeesModelToDto () {
        return new PropertyMap<DealFees, DealFeesDto>() {
            @Override
            protected void configure() {
                map().setDebtModelId(source.getDebtModel().getId());
            }
        };
    }

    //InterestUndrawnCapital
    PropertyMap<InterestUndrawnCapitalDto,InterestUndrawnCapital> mapInterestUndrawnDtoToModel(){
        return  new PropertyMap<InterestUndrawnCapitalDto,InterestUndrawnCapital>() {
            @Override
            protected void configure() {
                map().getDebtModel().setId(source.getDebtModelId());
            }
        };
    }

    PropertyMap<InterestUndrawnCapital,InterestUndrawnCapitalDto> mapInterestUndrawnModeltoDto () {
        return new PropertyMap<InterestUndrawnCapital,InterestUndrawnCapitalDto>() {
            @Override
            protected void configure() {
                map().setDebtModelId(source.getDebtModel().getId());
            }
        };
    }

    //Skim
    PropertyMap<SkimsDto,Skims> mapSkimsDtoToModel(){
        return  new PropertyMap<SkimsDto, Skims>() {
            @Override
            protected void configure() {
                map().getDebtModel().setId(source.getDebtModelId());
            }
        };
    }

    PropertyMap<Skims,SkimsDto> mapSkimsModelToDto () {
        return new PropertyMap<Skims,SkimsDto>() {
            @Override
            protected void configure() {
                map().setDebtModelId(source.getDebtModel().getId());
            }
        };
    }

    //Call Premium
    PropertyMap<CallPremiumDto,CallPremium> mapCallPremiumDtoToModel(){
        return  new PropertyMap<CallPremiumDto, CallPremium>() {
            @Override
            protected void configure() {
                map().getDebtModel().setId(source.getDebtModelId());
            }
        };
    }

    PropertyMap<CallPremium,CallPremiumDto> mapCallPremiumModelToDto () {
        return new PropertyMap<CallPremium,CallPremiumDto>() {
            @Override
            protected void configure() {
                map().setDebtModelId(source.getDebtModel().getId());
            }
        };
    }

    //DiscountRate
    PropertyMap<DiscountRateComputationDto,DiscountRateComputaion> mapDiscountRateComputationDtoToModel () {
        return new PropertyMap<DiscountRateComputationDto,DiscountRateComputaion>() {
            @Override
            protected void configure() {
                map().getDebtModel().setId(source.getDebtModelId());
            }
        };
    }

    PropertyMap<DiscountRateComputaion,DiscountRateComputationDto> mapDiscountRateComputationModelToDto () {
        return new PropertyMap<DiscountRateComputaion,DiscountRateComputationDto>() {
            @Override
            protected void configure() {
                map().setDebtModelId(source.getDebtModel().getId());
            }
        };
    }

    //DiscountAdjustment
    PropertyMap<DiscountAdjustmentDto,DiscountAdjustment> mapDiscountAdjustmentDtoToModel () {
        return new PropertyMap<DiscountAdjustmentDto,DiscountAdjustment>() {
            @Override
            protected void configure() {
                map().getDiscountRateComputation().setId(source.getDiscountRateComputationId());
            }
        };
    }

    PropertyMap<DiscountAdjustment,DiscountAdjustmentDto> mapDiscountAdjustmentModelToDto () {
        return new PropertyMap<DiscountAdjustment,DiscountAdjustmentDto>() {
            @Override
            protected void configure() {
                map().setDiscountRateComputationId(source.getDiscountRateComputation().getId());
            }
        };
    }

    //AnnualHistoricalFinancial
    PropertyMap<AnnualHistoricalFinancial,AnnualHistoricalFinancialDto> mapAnnualHistoricalFinancialModelToDto () {
        return new PropertyMap<AnnualHistoricalFinancial, AnnualHistoricalFinancialDto>() {
            @Override
            protected void configure() {
                map().setIssuerFinancialId(source.getIssuerFinancial().getId());
            }
        };
    }


    PropertyMap<AnnualHistoricalFinancialDto,AnnualHistoricalFinancial> mapAnnualHistoricalFinancialDtoTOModel () {
        return new PropertyMap<AnnualHistoricalFinancialDto,AnnualHistoricalFinancial>() {
            @Override
            protected void configure() {
                map().getIssuerFinancial().setId(source.getIssuerFinancialId());
            }
        };
    }

    //AnnualProjectedFinancial

    PropertyMap<AnnualProjectedFinancialDto,AnnualProjectedFinancial> mapAnnualProjectedFinancialDtoTOModel () {
        return new PropertyMap<AnnualProjectedFinancialDto,AnnualProjectedFinancial>() {
            @Override
            protected void configure() {
                map().getIssuerFinancial().setId(source.getIssuerFinancialId());
            }
        };
    }


    PropertyMap<AnnualProjectedFinancial,AnnualProjectedFinancialDto> mapAnnualProjectedFinancialModelToDto () {
        return new PropertyMap<AnnualProjectedFinancial,AnnualProjectedFinancialDto>() {
            @Override
            protected void configure() {
                map().setIssuerFinancialId(source.getIssuerFinancial().getId());
            }
        };
    }

    //IssuerFinancial

    PropertyMap<IssuerFinancial,IssuerFinancialDto> mapIssuerFinancialModelToDto () {
        return new PropertyMap<IssuerFinancial,IssuerFinancialDto>() {
            @Override
            protected void configure() {
                map().setDebtModelId(source.getDebtModel().getId());
            }
        };
    }

    PropertyMap<IssuerFinancialDto,IssuerFinancial> mapIssuerFinancialDtoToModel () {
        return new PropertyMap<IssuerFinancialDto,IssuerFinancial>() {
            @Override
            protected void configure() {
                map().getDebtModel().setId(source.getDebtModelId());
            }
        };
    }

    //CustomizableCashflow
    PropertyMap<CustomizableCashflowDto, CustomizableCashflow> mapCustomizableCashflowDtoToModel(){
        return  new PropertyMap<CustomizableCashflowDto, CustomizableCashflow>() {
            @Override
            protected void configure() {
                map().getDebtModel().setId(source.getDebtModelId());
            }
        };
    }

    PropertyMap< CustomizableCashflow,CustomizableCashflowDto> mapCustomizableCashflowModelToDto () {
        return new PropertyMap<CustomizableCashflow,CustomizableCashflowDto>() {
            @Override
            protected void configure() {
                map().setDebtModelId(source.getDebtModel().getId());
            }
        };
    }

    //CustomizableCashflowExcel
    PropertyMap<CustomizableCashflowExcelDto,CustomizableCashflowExcel> mapCustomizableCashflowExcelDtoToModel () {
        return new PropertyMap<CustomizableCashflowExcelDto,CustomizableCashflowExcel>() {
            @Override
            protected void configure() {
                map().getDebtModel().setId(source.getDebtModelId());
            }
        };
    }

    PropertyMap<CustomizableCashflowExcel, CustomizableCashflowExcelDto> mapCustomizableCashflowExcelModelToDto () {
        return new PropertyMap<CustomizableCashflowExcel, CustomizableCashflowExcelDto>() {
            @Override
            protected void configure() {
                map().setDebtModelId(source.getDebtModel().getId());
            }
        };
    }

    //InterimPaymentDetails
    PropertyMap<InterimPaymentDetailsDto,InterimPaymentDetails> mapInterimPaymentDetailsDtoToModel () {
        return new PropertyMap<InterimPaymentDetailsDto,InterimPaymentDetails>() {
            @Override
            protected void configure() {
                map().getCustomizableCashflowExcel().setId(source.getCustomizableCashflowExcelId());
            }
        };
    }

    PropertyMap<InterimPaymentDetails,InterimPaymentDetailsDto> mapInterimPaymentDetailsModelToDto () {
        return new PropertyMap<InterimPaymentDetails,InterimPaymentDetailsDto>() {
            @Override
            protected void configure() {
                map().setCustomizableCashflowExcelId(source.getCustomizableCashflowExcel().getId());
            }
        };
    }

    //LookupDebtDetails
    PropertyMap<LookUpDebtDetailsDto,LookUpDebtDetails> mapLookUPDebtDetailsDtoToModel () {
        return new PropertyMap<LookUpDebtDetailsDto,LookUpDebtDetails>() {
            @Override
            protected void configure() {
                map().setCompanyId(source.getCompanyId());
            }
        };
    }

   //LookUpValuationDetails
   PropertyMap<LookUpValuationDetailsDto, LookUpValuationDetails> mapLookUpValuationDetailsDtoToModel () {
       return new PropertyMap<LookUpValuationDetailsDto, LookUpValuationDetails>() {
           @Override
           protected void configure() {
               map().getLookUpDebtDetails().setCompanyId(source.getCompany_id());
           }
       };
   }

    PropertyMap< LookUpValuationDetails,LookUpValuationDetailsDto> mapLookUpValuationDetailsModelToDto () {
        return new PropertyMap<LookUpValuationDetails,LookUpValuationDetailsDto>() {
            @Override
            protected void configure() {
                map().setCompany_id(source.getLookUpDebtDetails().getCompanyId());
            }
        };
    }



}
