package com.coach.flame.date

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

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
            throw IllegalArgumentException("Invalid date format. Date: $date.", ex)
        }

    }

    /**
     * Convert string date (yyyy-MM-ddTHH:MM:ss) into a [ZonedDateTime]
     *
     * @param date with format [DateTimeFormatter.ISO_OFFSET_DATE_TIME] e.g: 2021-07-14T16:52:52.389929+01:00
     *
     * @return [ZonedDateTime] instance
     * @throws IllegalArgumentException when the date parameter has a wrong format
     */
    fun toZonedDateTime(date: String): ZonedDateTime {

        try {
            return ZonedDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        } catch (ex: DateTimeParseException) {
            throw IllegalArgumentException("Invalid date format. Date: $date.", ex)
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
     * Convert zoned date time in another timezone
     *
     * @param zonedDateTime date to convert
     * @param timezone final time zone
     *
     * @return [ZonedDateTime] in the [timezone] defined
     */
    fun toAnotherZone(zonedDateTime: ZonedDateTime, timezone: ZoneId): ZonedDateTime {
        return zonedDateTime.withZoneSameInstant(timezone)
    }

    /**
     * Convert system date in another timezone
     *
     * @param date date to convert
     * @param timezone final time zone
     *
     * @return [ZonedDateTime] in the [timezone] defined
     */
    fun toAnotherZone(date: LocalDateTime, timezone: ZoneId): ZonedDateTime {
        val systemTimeZone = date.atZone(ZoneId.systemDefault())
        return systemTimeZone.withZoneSameInstant(timezone)
    }

    /**
     * Convert [ZonedDateTime] for the format [DateTimeFormatter.ISO_LOCAL_DATE_TIME]
     * without offset
     *
     * @param date
     *
     * @return formatted date using the [DateTimeFormatter.ISO_LOCAL_DATE_TIME] formatter
     */
    fun toISODate(date: ZonedDateTime): String {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(date)
    }

    /**
     * Convert [ZonedDateTime] for the format [DateTimeFormatter.ISO_OFFSET_DATE_TIME]
     * with offset
     *
     * @param date
     *
     * @return formatted date using the [DateTimeFormatter.ISO_OFFSET_DATE_TIME] formatter
     */
    fun toISODateWithOffset(date: ZonedDateTime): String {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(date)
    }
}

