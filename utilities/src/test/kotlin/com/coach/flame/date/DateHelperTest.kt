package com.coach.flame.date

import com.coach.flame.date.DateHelper.toISODate
import com.coach.flame.date.DateHelper.toDate
import com.coach.flame.date.DateHelper.toUTCDate
import org.assertj.core.api.BDDAssertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneId

class DateHelperTest {

    @Test
    fun `test convert string to local date`() {
        thenNoException().isThrownBy { toDate("2020-12-05") }
    }

    @Test
    fun `test convert invalid string to local date`() {
        thenExceptionOfType(IllegalArgumentException::class.java).isThrownBy { toDate("05-12-2020") }
        thenExceptionOfType(IllegalArgumentException::class.java).isThrownBy { toDate("20") }
    }

    @Test
    fun `test convert date for UTC`() {
        val utcDate = toUTCDate(LocalDateTime.parse("2021-05-10T10:00:01"))
        then(utcDate.zone).isEqualTo(ZoneId.of("UTC"))
    }

    @Test
    fun `test convert date to string using ISO formatter`() {
        val utcDate = toUTCDate(LocalDateTime.parse("2021-05-10T10:00:01"))
        val dateFormatted = toISODate(utcDate)
        then(dateFormatted).isEqualTo("2021-05-10T10:00:01")
    }
}
