package com.coach.flame.date

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * TODO: Write documentation
 */
fun stringToDate(date: String): LocalDate {

    try {

        val formatter = DateTimeFormatter.ofPattern("yyy-MM-dd")
        return LocalDate.parse(date, formatter)

    } catch (ex: DateTimeParseException) {
        throw IllegalArgumentException("Invalid format date. Date: $date", ex)
    }

}