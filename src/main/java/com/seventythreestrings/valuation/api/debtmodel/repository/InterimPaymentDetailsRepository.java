package com.seventythreestrings.valuation.api.debtmodel.repository;

import com.seventythreestrings.valuation.api.debtmodel.model.InterimPaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InterimPaymentDetailsRepository extends JpaRepository<InterimPaymentDetails, Long>, JpaSpecificationExecutor<InterimPaymentDetails> {

}
