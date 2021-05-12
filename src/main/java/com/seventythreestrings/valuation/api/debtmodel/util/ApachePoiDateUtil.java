// ref: https://github.com/apache/poi/blob/b6aee1ef6d3e92a28ffd4b5c03e677b63b43747f/poi/src/main/java/org/apache/poi/ss/usermodel/DateUtil.java
/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package com.seventythreestrings.valuation.api.debtmodel.util;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;

public class ApachePoiDateUtil {
    public static final int SECONDS_PER_MINUTE = 60;
    public static final int MINUTES_PER_HOUR = 60;
    public static final int HOURS_PER_DAY = 24;
    public static final int SECONDS_PER_DAY = (HOURS_PER_DAY * MINUTES_PER_HOUR * SECONDS_PER_MINUTE);

    // used to specify that date is invalid
    private static final int BAD_DATE         = -1;
    public static final long DAY_MILLISECONDS = SECONDS_PER_DAY * 1000L;

    /**
     * A simplified implementation of ApachePoi DateUtil
     */
    public static void setCalendar(Calendar calendar, int wholeDays) {
        int startYear = 1900;
        int dayAdjust = -1;
        if (wholeDays < 61) {
            // Date is prior to 3/1/1900, so adjust because Excel thinks 2/29/1900 exists
            // If Excel date == 2/29/1900, will become 3/1/1900 in Java representation
            dayAdjust = 0;
        }
        calendar.set(startYear,0, wholeDays + dayAdjust, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.clear(Calendar.MILLISECOND);
    }

    public static double getExcelDate(LocalDate date) {
        int year = date.getYear();
        int dayOfYear = date.getDayOfYear();
        int hour = 0;
        int minute = 0;
        int second = 0;
        int milliSecond = 0;

        return internalGetExcelDate(year, dayOfYear, hour, minute, second, milliSecond, false);
    }

    public static double getExcelDate(Calendar calendar) {
        LocalDate date = LocalDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId()).toLocalDate();
        return getExcelDate(date);
    }

    private static double internalGetExcelDate(int year, int dayOfYear, int hour, int minute, int second, int milliSecond, boolean use1904windowing) {
        if ((!use1904windowing && year < 1900) ||
                (use1904windowing && year < 1904))
        {
            return BAD_DATE;
        }

        // Because of daylight time saving we cannot use
        //     date.getTime() - calStart.getTimeInMillis()
        // as the difference in milliseconds between 00:00 and 04:00
        // can be 3, 4 or 5 hours but Excel expects it to always
        // be 4 hours.
        // E.g. 2004-03-28 04:00 CEST - 2004-03-28 00:00 CET is 3 hours
        // and 2004-10-31 04:00 CET - 2004-10-31 00:00 CEST is 5 hours
        double fraction = (((hour * 60.0
                + minute
        ) * 60.0 + second
        ) * 1000.0 + milliSecond
        ) / DAY_MILLISECONDS;

        double value = fraction + absoluteDay(year, dayOfYear, use1904windowing);

        if (!use1904windowing && value >= 60) {
            value++;
        } else if (use1904windowing) {
            value--;
        }

        return value;
    }

    private static int absoluteDay(int year, int dayOfYear, boolean use1904windowing) {
        return dayOfYear + daysInPriorYears(year, use1904windowing);
    }

    private static int daysInPriorYears(int yr, boolean use1904windowing)
    {
        if ((!use1904windowing && yr < 1900) || (use1904windowing && yr < 1904)) {
            throw new IllegalArgumentException("'year' must be 1900 or greater");
        }

        int yr1  = yr - 1;
        int leapDays =   yr1 / 4   // plus julian leap days in prior years
                - yr1 / 100 // minus prior century years
                + yr1 / 400 // plus years divisible by 400
                - 460;      // leap days in previous 1900 years

        return 365 * (yr - (use1904windowing ? 1904 : 1900)) + leapDays;
    }
}
