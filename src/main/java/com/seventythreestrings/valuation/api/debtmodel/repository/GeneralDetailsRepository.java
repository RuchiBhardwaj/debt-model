package com.seventythreestrings.valuation.api.debtmodel.repository;

import com.seventythreestrings.valuation.api.debtmodel.model.GeneralDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@Repository
public interface GeneralDetailsRepository extends JpaRepository<GeneralDetails, Long>, JpaSpecificationExecutor<GeneralDetails> {

    Optional<GeneralDetails> findFirstByDebtModelId(Long debtModelId);

    @Query(value = "select gd.* from general_details gd where gd.debt_model_id=:debtModelId",nativeQuery = true)
    GeneralDetails findFirstByDebtModelIdCompanyDetails(Long debtModelId);

    @Query(value = "select gd.* from general_details gd where gd.debt_model_id=:debtModelId",nativeQuery = true)
//    @Query(value = "select gd from GeneralDetails gd where gd.debtModelId=:debtModelId")
    GeneralDetails findFirstByDebtModelIdAndValuationDate(@Param("debtModelId")Long debtModelId);
}
