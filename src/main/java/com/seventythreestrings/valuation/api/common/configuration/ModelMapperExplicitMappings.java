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

}
