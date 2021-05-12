package com.seventythreestrings.valuation.api.debtmodel.util;

import com.seventythreestrings.valuation.api.debtmodel.dto.PaymentFrequency;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static int getMonthIncrementForPaymentFrequency(PaymentFrequency paymentFrequency) {
        int increment = 0;
        switch (paymentFrequency) {
            case MONTHLY:
                increment = 1;
                break;
            case QUARTERLY:
                increment = 3;
                break;
            case SEMI_ANNUAL:
                increment = 6;
                break;
            case ANNUAL:
                increment = 12;
                break;
            default:
                break;
        }

        return increment;
    }

    public static LocalDate addMonths(LocalDate localDate, int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getDateForLocalDate(localDate));
        calendar.add(Calendar.MONTH, months);

        return getLocalDateForDate(calendar.getTime());
    }

    public static Date getDateForLocalDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate getLocalDateForDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
