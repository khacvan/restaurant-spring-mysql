package com.restaurant.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateUtils {

    /**
     * Calculates the number of days between two dates (inclusive) to determine the difference.
     *
     * @param firstDate The first date.
     * @param secondDate The second date.
     * @return The number of days between the two dates (inclusive).
     */
    public static long getDaysToDelete(Date firstDate, Date secondDate) {
        // Convert Dates to LocalDates to remove time components and calculate days
        LocalDate localDateA = firstDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDateB = secondDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Calculate the difference between two LocalDates and return the number of days
        return Duration.between(localDateA.atStartOfDay(), localDateB.atStartOfDay()).toDays();
    }


}
