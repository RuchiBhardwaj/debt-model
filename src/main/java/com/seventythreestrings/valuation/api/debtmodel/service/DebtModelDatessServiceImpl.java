package com.seventythreestrings.valuation.api.debtmodel.service;


import com.google.common.collect.Comparators;
import com.seventythreestrings.valuation.api.debtmodel.dto.*;
import com.seventythreestrings.valuation.api.debtmodel.dto.DebtModelEnums.FrequencyOfPaymentType;
import com.seventythreestrings.valuation.api.exception.AppException;
import com.seventythreestrings.valuation.api.exception.ErrorCodesAndMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.ValueRange;
import java.util.*;

@Service
@Slf4j
public class DebtModelDatessServiceImpl implements DebtModelDatessService {
		/**
	 * Keep a notation which says paymentDay= 99 is endo f month
	 */
	@Override
	public List<DebtModel> getDatePlotsFromDTO(GeneralDetails generalDetails, InterestDetails interestDetails,
			PrepaymentDetails prepaymentDetails) throws AppException{
		try {
			LocalDate originationDate = generalDetails.getOriginationDate();
			FrequencyOfPaymentType frequencyOfTheInterestPayment =DebtModelEnums.FrequencyOfPaymentType.valueOf(interestDetails.getInterestPaymentFrequency());
			int incrementer=1;
			switch(frequencyOfTheInterestPayment) {
			case MONTHLY:
				incrementer = 1;
				break;
			case QUARTERLY:
				incrementer = 3;
				break;
			case SEMI_ANNUAL:
				incrementer = 6;
				break;
			case ANNUAL:
				incrementer = 12;
				break;

			}
			//LocalDate dateSecondInterestPaymentDate = LocalDate.of(interestDetails.getFirstInterestPaymentDate().getYear(),(interestDetails.getFirstInterestPaymentDate().getMonth().getValue()+incrementer)%12==0?12:((interestDetails.getFirstInterestPaymentDate().getMonth().getValue()+incrementer)%12), interestDetails.getInterestPaymentDay()==99?28:interestDetails.getInterestPaymentDay());
			//LocalDate dateSecondInterestPaymentDate = LocalDate.of(interestDetails.getFirstInterestPaymentDate().getYear(),(interestDetails.getFirstInterestPaymentDate().getMonth().getValue()+incrementer)%12==0?12:((interestDetails.getFirstInterestPaymentDate().getMonth().getValue()+incrementer)%12), interestDetails.getInterestPaymentDay()==99?28:interestDetails.getInterestPaymentDay());
			LocalDate dateSecondInterestPaymentDate = LocalDate.of(((((interestDetails.getFirstInterestPaymentDate().getMonth().getValue()+incrementer)/12.0)>1)?(interestDetails.getFirstInterestPaymentDate().getYear()+1):interestDetails.getFirstInterestPaymentDate().getYear()),(interestDetails.getFirstInterestPaymentDate().getMonth().getValue()+incrementer)%12==0?12:((interestDetails.getFirstInterestPaymentDate().getMonth().getValue()+incrementer)%12), interestDetails.getInterestPaymentDay()==99?28:interestDetails.getInterestPaymentDay());
			if(interestDetails.getInterestPaymentDay() == 99) {
			ValueRange range1 = dateSecondInterestPaymentDate.range(ChronoField.DAY_OF_MONTH);
			Long max1 = range1.getMaximum();
			LocalDate newDate1 = dateSecondInterestPaymentDate.withDayOfMonth(max1.intValue());
			dateSecondInterestPaymentDate = newDate1;
			}
			LinkedHashMap<LocalDate,String> dateMap = new LinkedHashMap<>();
			LocalDate loopDate = dateSecondInterestPaymentDate;
			dateMap.put(originationDate, "O");
			dateMap.put(interestDetails.getFirstInterestPaymentDate(), "I");
			dateMap.put(loopDate, "I");
			while(loopDate.isBefore(interestDetails.getLastInterestPaymentDate()) || loopDate.equals(interestDetails.getLastInterestPaymentDate())) {
				loopDate=LocalDate.of((loopDate.getMonth().getValue()+incrementer)/12.0>1?(loopDate.getYear()+1):loopDate.getYear(),(loopDate.getMonth().getValue()+incrementer)%12==0?12:((loopDate.getMonth().getValue()+incrementer)%12), interestDetails.getInterestPaymentDay()==99?28:interestDetails.getInterestPaymentDay());
				if(interestDetails.getInterestPaymentDay() == 99) {
					ValueRange range = loopDate.range(ChronoField.DAY_OF_MONTH);
					Long max = range.getMaximum();
					LocalDate newDate = loopDate.withDayOfMonth(max.intValue());
					loopDate = newDate;
				}
				if(!loopDate.isAfter(interestDetails.getLastInterestPaymentDate())) {
					dateMap.put(loopDate, "I");
					log.info(loopDate+"");
				}else {
					dateMap.put(interestDetails.getLastInterestPaymentDate(), "I");
					log.info(interestDetails.getLastInterestPaymentDate()+"");
				}
			}

			if(!Comparators.isInOrder(dateMap.keySet(), new Comparator<LocalDate>() {

				@Override
				public int compare(LocalDate o1, LocalDate o2) {
					// TODO Auto-generated method stub
					return o1.compareTo(o2);
				}
			}))
				AppException.newAppException(ErrorCodesAndMessages.DEBT_MODEL_DATE_ERROR);

			List<InterimVariablePayment> varPayDates = prepaymentDetails.getInterimVariablePayments();
			for(InterimVariablePayment payDates:varPayDates) {
				if(payDates.getDate().isBefore(originationDate)||payDates.getDate().isAfter(generalDetails.getMaturityDate()))
					AppException.newAppException(ErrorCodesAndMessages.DEBT_MODEL_DATE_INVALID);
				if(dateMap.get(payDates.getDate())!=null)
				{
					dateMap.put(payDates.getDate(), DateType.IP.toString());
				}else {
					dateMap.put(payDates.getDate(), DateType.P.toString());
				}
			}
			TreeMap<LocalDate, String> treeMap = new TreeMap<LocalDate, String>();
			treeMap.putAll(dateMap);

			 Iterator<?> hmIterator = treeMap.entrySet().iterator();
		      List<DebtModel> debtModelList = new LinkedList<>();
		        while (hmIterator.hasNext()) {
		            Map.Entry mapElement = (Map.Entry)hmIterator.next();
		            DebtModel debtModel = new DebtModel();
		            debtModel.setDate(LocalDate.parse(mapElement.getKey()+""));
		            debtModel.setDateType(DateType.valueOf(mapElement.getValue()+""));
		            debtModelList.add(debtModel);
		        }
		      //  log.info("debtModelList:"+debtModelList);
			return debtModelList;
		} catch (Exception e) {
			//log.error(ErrorCodesAndMessages.DEBT_MODEL_DATE_ERROR.getMessage()+" : "+e.getMessage(),e);
			AppException.newAppException(ErrorCodesAndMessages.DEBT_MODEL_DATE_ERROR);
		}
		return null;
	}

}
