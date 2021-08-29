package com.seventythreestrings.valuation.api.debtmodel.util;

import com.seventythreestrings.valuation.api.debtmodel.enums.DayCountConvention;

import java.time.LocalDate;

public class CashflowUtil {
    public static double getPartialPeriod(LocalDate startDate, LocalDate endDate, DayCountConvention dayCountConvention) {
        return ApachePoiYearFracCalculator.calculate(ApachePoiDateUtil.getExcelDate(startDate), ApachePoiDateUtil.getExcelDate(endDate), dayCountConvention.getBasis());
    }

    public static double getDiscountingFactor(double discountRate, double partialPeriod) {
        return 1 / Math.pow((1 + discountRate / 100), partialPeriod);
    }

    public static double getPresentValue(double amount, double discountingFactor, LocalDate date, LocalDate minDate) {
        if (date.isBefore(minDate)) {
            return 0;
        }
        return amount * discountingFactor;
    }

    public static double getInterestOutflow(LocalDate startDate, LocalDate endDate, DayCountConvention dayCountConvention, double amount, double interestRate) {
        if (startDate.equals(endDate)) {
            return 0;
        }
        double period = getPartialPeriod(startDate, endDate, dayCountConvention);

        return amount * period * interestRate / 100;
    }
}
