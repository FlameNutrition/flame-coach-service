package com.coach.flame.date

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object DateHelper {

    /**
     * Convert string date (yyyy-MM-dd) into a [LocalDate]
     *
     * @param date with format yyyy-mm-dd e.g: 2021-04-10
     *
     * @return [LocalDate] instance
     * @throws IllegalArgumentException when the date parameter has a wrong format
     */
    fun toDate(date: String): LocalDate {

        try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            return LocalDate.parse(date, formatter)
        } catch (ex: DateTimeParseException) {
            throw IllegalArgumentException("Invalid format date. Date: $date", ex)
        }

    }

    /**
     * Get the UTC date using a [LocalDateTime]
     *
     * @param date
     *
     * @return [ZonedDateTime] UTC date
     */
    fun toUTCDate(date: LocalDateTime): ZonedDateTime {
        return ZonedDateTime.of(date, ZoneId.of("UTC"))
    }

    /**
     * Convert [ZonedDateTime] for the format [DateTimeFormatter.ISO_LOCAL_DATE_TIME]
     *
     * @param date
     *
     * @return formatted date using the [DateTimeFormatter.ISO_LOCAL_DATE_TIME] formatter
     */
    fun toISODate(date: ZonedDateTime): String {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(date)
    }
}

