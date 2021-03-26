package com.seventythreestrings.valuation.api.debtmodel.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seventythreestrings.valuation.api.debtmodel.dto.*;
import com.seventythreestrings.valuation.api.exception.AppException;
import com.seventythreestrings.valuation.api.exception.ErrorCodesAndMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DebtModelServiceImpl implements DebtModelService {
	@Inject
	private ObjectMapper objectMapper;
	@Inject
	private DebtModelDatessService debtModelDatessService;
	
	@Override
	public List<DebtModel> computeDeptModel(String requestBody) throws AppException{
		try {
			JsonNode requestBodyNode=objectMapper.readTree(requestBody);
			GeneralDetails generalDetails=objectMapper.treeToValue(requestBodyNode.get("generalDetails"), GeneralDetails.class);
			InterestDetails interestDetails=objectMapper.treeToValue(requestBodyNode.get("interestDetails"), InterestDetails.class);
			PrepaymentDetails prepaymentDetails=objectMapper.treeToValue(requestBodyNode.get("prepaymentDetails"), PrepaymentDetails.class);
			List<DebtModel> debtModels=debtModelDatessService.getDatePlotsFromDTO(generalDetails, interestDetails, prepaymentDetails);
			log.info("Debt Models: "+objectMapper.writeValueAsString(debtModels));
			List<DebtModel> chashMovementResult=null;
			if(InterestPaidType.PAID==InterestPaidType.valueOf(interestDetails.getInterestPaidOrAccrued())) {
				chashMovementResult=computeCashMovementForPaid(generalDetails, interestDetails, debtModels, prepaymentDetails.getInterimVariablePayments());
			}else {
				chashMovementResult=computeCashMovementForAccrued(generalDetails, interestDetails, debtModels, prepaymentDetails.getInterimVariablePayments());
			}
			return chashMovementResult;
		} catch (AppException e) {
			AppException.newAppException(e.getErrorCodesAndMessages());
		} catch (Exception e) {
			log.error(ErrorCodesAndMessages.DEBT_MODEL_ERROR.getMessage()+" : "+e.getMessage(),e);
			AppException.newAppException(ErrorCodesAndMessages.DEBT_MODEL_ERROR);
		}
		return null;
	}
	
	@Override
	public double computeBaseRate(InterestDetails interestDetails) throws AppException{
		try {
			double rate=0.0;
			if(interestDetails.getBaseRate().equalsIgnoreCase("FIXED")){
				rate=interestDetails.getFixedBaseRate();
			} else if (interestDetails.getBaseRate().equalsIgnoreCase("LIBOR")){
				rate=computeLiboreCurve(interestDetails);
			}
			double baseRate=Math.min(Math.max(rate, interestDetails.getFloor()), interestDetails.getCap());
			//double baseRate=Math.min(Math.max(1.44, interestDetails.getFloor()), interestDetails.getCap());
			log.info("Exit from computeBaseRate with result :"+baseRate);
			return baseRate;
		} catch (Exception e) {
			log.error(ErrorCodesAndMessages.DEBT_MODEL_BASE_RATE_ERROR.getMessage()+" : "+e.getMessage(),e);
			AppException.newAppException(ErrorCodesAndMessages.DEBT_MODEL_BASE_RATE_ERROR);
		}
		return 0;
	}
	
	@Override
	public double computeLiboreCurve(InterestDetails interestDetails) throws AppException{
		try {
			//as of now we are generating random numbers. bet this value should come from Market API
			return Math.random() * ( 1.5) + 0.5;
		} catch (Exception e) {
			log.error(ErrorCodesAndMessages.DEBT_MODEL_LIBORE_CURVE_ERROR.getMessage()+" : "+e.getMessage(),e);
			AppException.newAppException(ErrorCodesAndMessages.DEBT_MODEL_LIBORE_CURVE_ERROR);
		}
		return 0;
	}

	@Override
	public List<DebtModel> computeCashMovementForPaid(GeneralDetails generalDetails,InterestDetails interestDetails,List<DebtModel> debtModels,List<InterimVariablePayment> interimVariablePayments) throws AppException{
		try {
			double baseRate=computeBaseRate(interestDetails);
			double spread=interestDetails.getSpreadOverBaseRate();
			
			DebtModel firstDebtModel=debtModels.get(0);
			firstDebtModel.setTotalPricipalOutstanding(generalDetails.getPrincipalAmount());
			computeInterestRate(baseRate, spread, firstDebtModel);

			double outStandingPrincipal=generalDetails.getPrincipalAmount();
			firstDebtModel.setTotalPricipalOutstanding(outStandingPrincipal);
			firstDebtModel.setPrincipalInflow(outStandingPrincipal);
			//DateType lastDateType=DateType.O;
			DebtModel lastDebtModel=firstDebtModel;
			
			List<DebtModel> repaymentList=new ArrayList<DebtModel>();
			
			for(DebtModel debtModel:debtModels) {
				if(debtModel.getDateType()!=DateType.O) {
					computeInterestRate(baseRate, spread, debtModel);
					
					double yearFrac=couputeYearFrac(lastDebtModel.getDate(), debtModel.getDate());
					double interest=computeInterest(lastDebtModel.getTotalPricipalOutstanding(), yearFrac, lastDebtModel.getTotalInterestRate());
					debtModel.setYearFrac(yearFrac);
					debtModel.setInterestPayment(interest);
					
					if(debtModel.getDateType()==DateType.I) {
						if(DateType.I==lastDebtModel.getDateType()||DateType.O==lastDebtModel.getDateType()) {
							debtModel.setInterestOutflow(interest);
						}else {
							debtModel.setInterestOutflow(computeTotalInterest(repaymentList,interest));
						}
						debtModel.setTotalPricipalOutstanding(lastDebtModel.getTotalPricipalOutstanding());
						repaymentList.clear();
						
					}else if(debtModel.getDateType()==DateType.P) {
						double paymentAmount=getRepaymentAmount(debtModel.getDate(), interimVariablePayments);
						debtModel.setPricipalRepayment(paymentAmount);
						debtModel.setTotalPricipalOutstanding(lastDebtModel.getTotalPricipalOutstanding()-paymentAmount);
						repaymentList.add(debtModel);
						
					}else if(debtModel.getDateType()==DateType.IP) {
						if(DateType.I==lastDebtModel.getDateType()||DateType.O==lastDebtModel.getDateType()) {
							debtModel.setInterestOutflow(interest);
						}else {
							debtModel.setInterestOutflow(computeTotalInterest(repaymentList,interest));
						}
						repaymentList.clear();
						double paymentAmount=getRepaymentAmount(debtModel.getDate(), interimVariablePayments);
						debtModel.setPricipalRepayment(paymentAmount);
						debtModel.setTotalPricipalOutstanding(lastDebtModel.getTotalPricipalOutstanding()-paymentAmount);
						repaymentList.add(debtModel);
					}
					lastDebtModel=debtModel;
				}
				debtModel.setTotalCashMovement(-debtModel.getPrincipalInflow()+debtModel.getInterestOutflow()+debtModel.getPricipalRepayment());
			}
		} catch (Exception e) {
			log.error(ErrorCodesAndMessages.DEBT_MODEL_CASH_MOVEMENT_ERROR.getMessage()+" : "+e.getMessage(),e);
			AppException.newAppException(ErrorCodesAndMessages.DEBT_MODEL_CASH_MOVEMENT_ERROR);
		}
		return debtModels;
	}
	
	@Override
	public List<DebtModel> computeCashMovementForAccrued(GeneralDetails generalDetails,InterestDetails interestDetails,List<DebtModel> debtModels,List<InterimVariablePayment> interimVariablePayments) throws AppException{
		try {
			double baseRate=computeBaseRate(interestDetails);
			double spread=interestDetails.getSpreadOverBaseRate();
			
			DebtModel firstDebtModel=debtModels.get(0);
			firstDebtModel.setTotalPricipalOutstanding(generalDetails.getPrincipalAmount());
			computeInterestRate(baseRate, spread, firstDebtModel);

			double outStandingPrincipal=generalDetails.getPrincipalAmount();
			firstDebtModel.setTotalPricipalOutstanding(outStandingPrincipal);
			firstDebtModel.setPrincipalInflow(outStandingPrincipal);
			//DateType lastDateType=DateType.O;
			DebtModel lastDebtModel=firstDebtModel;
					
			for(DebtModel debtModel:debtModels) {
				if(debtModel.getDateType()!=DateType.O) {
					computeInterestRate(baseRate, spread, debtModel);
					
					double yearFrac=couputeYearFrac(lastDebtModel.getDate(), debtModel.getDate());
					double interest=computeInterest(lastDebtModel.getTotalPricipalOutstanding(), yearFrac, lastDebtModel.getTotalInterestRate());
					debtModel.setYearFrac(yearFrac);
					debtModel.setInterestPayment(interest);
					
					if(debtModel.getDateType()==DateType.I) {
						debtModel.setTotalPricipalOutstanding(lastDebtModel.getTotalPricipalOutstanding()+interest);
					}else if(debtModel.getDateType()==DateType.P ||debtModel.getDateType()==DateType.IP) {
						double paymentAmount=getRepaymentAmount(debtModel.getDate(), interimVariablePayments);
						debtModel.setPricipalRepayment(paymentAmount);
						debtModel.setTotalPricipalOutstanding(lastDebtModel.getTotalPricipalOutstanding()-paymentAmount+interest);
					}
					lastDebtModel=debtModel;
				}
				debtModel.setTotalCashMovement(-debtModel.getPrincipalInflow()+debtModel.getInterestOutflow()+debtModel.getPricipalRepayment());
			}
		} catch (Exception e) {
			log.error(ErrorCodesAndMessages.DEBT_MODEL_CASH_MOVEMENT_ERROR.getMessage()+" : "+e.getMessage(),e);
			AppException.newAppException(ErrorCodesAndMessages.DEBT_MODEL_CASH_MOVEMENT_ERROR);
		}
		return debtModels;
	}
	
	private double getRepaymentAmount(LocalDate paymentDate,List<InterimVariablePayment> interimVariablePayments) throws AppException{
		try {
			List<InterimVariablePayment> repaymentList=interimVariablePayments.stream().filter(predicate->predicate.getDate().isEqual(paymentDate)).collect(Collectors.toList());
			return repaymentList.get(0).getAmount();
		} catch (Exception e) {
			log.error(ErrorCodesAndMessages.DEBT_MODEL_FETCHING_REPAYMENT_AMOUNT_ERROR.getMessage()+" : "+e.getMessage(),e);
			AppException.newAppException(ErrorCodesAndMessages.DEBT_MODEL_FETCHING_REPAYMENT_AMOUNT_ERROR);
		}
		return 0;
	}
	private double computeTotalInterest(List<DebtModel> repaymentList,double interest) throws AppException{
		try {
			double totalInterest=0;
			for(DebtModel repayment:repaymentList) {
				totalInterest+=repayment.getInterestPayment();
			}
			totalInterest+=interest;
			return totalInterest;
		} catch (Exception e) {
			log.error(ErrorCodesAndMessages.DEBT_MODEL_TOTAL_INTEREST_ERROR.getMessage()+" : "+e.getMessage(),e);
			AppException.newAppException(ErrorCodesAndMessages.DEBT_MODEL_TOTAL_INTEREST_ERROR);
		}
		return 0;
	}
	private DebtModel computeInterestRate(double baseRate,double spread,DebtModel debtModel) throws AppException{
		try {
			debtModel.setBaseRate(baseRate);
			debtModel.setSpread(spread);
			debtModel.setTotalInterestRate(baseRate+spread);
			return debtModel;
		} catch (Exception e) {
			log.error(ErrorCodesAndMessages.DEBT_MODEL_INTEREST_RATE_ERROR.getMessage()+" : "+e.getMessage(),e);
			AppException.newAppException(ErrorCodesAndMessages.DEBT_MODEL_INTEREST_RATE_ERROR);
		}
		return null;
	}
	
	private double couputeYearFrac(LocalDate startDate,LocalDate endDate) throws AppException{
		try {
			long noOfDaysBetween =startDate.until(endDate, ChronoUnit.DAYS);
			double yearFrac=noOfDaysBetween/365d;
			return yearFrac;
		} catch (Exception e) {
			log.error(ErrorCodesAndMessages.DEBT_MODEL_YEAR_FRAC_ERROR.getMessage()+" : "+e.getMessage(),e);
			AppException.newAppException(ErrorCodesAndMessages.DEBT_MODEL_YEAR_FRAC_ERROR);
		}
		return 0;
	}
	
	private double computeInterest(double principal,double yearFrac,double interestRate) throws AppException{
		try {
			double interest=principal*yearFrac*(interestRate/100);
			return interest;
		} catch (Exception e) {
			log.error(ErrorCodesAndMessages.DEBT_MODEL_INTEREST_ERROR.getMessage()+" : "+e.getMessage(),e);
			AppException.newAppException(ErrorCodesAndMessages.DEBT_MODEL_INTEREST_ERROR);
		}
		return 0;
	}
}
