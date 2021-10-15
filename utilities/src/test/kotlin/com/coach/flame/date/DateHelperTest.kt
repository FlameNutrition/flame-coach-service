package com.coach.flame.date

import com.coach.flame.date.DateHelper.toAnotherZone
import com.coach.flame.date.DateHelper.toDate
import com.coach.flame.date.DateHelper.toISODate
import com.coach.flame.date.DateHelper.toISODateWithOffset
import com.coach.flame.date.DateHelper.toUTCDate
import com.coach.flame.date.DateHelper.toZonedDateTime
import org.assertj.core.api.BDDAssertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

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

    //FIXME: Please enable me and find why I'm failing at CircleCi
    @Disabled("Test is failing at CircleCi, this needs to be investigated")
    @Test
    fun `test convert date to string using timezone`() {

        val date = LocalDateTime.parse("2021-05-10T10:00:01")

        then(toAnotherZone(date, ZoneId.of("Europe/London")))
            .isEqualTo(ZonedDateTime.of(date, ZoneId.of("Europe/London")))
        then(toAnotherZone(date, ZoneId.of("Asia/Singapore")))
            .isEqualTo(ZonedDateTime.parse("2021-05-10T17:00:01+08:00"))
        then(toAnotherZone(date, ZoneId.of("UTC")))
            .isEqualTo(ZonedDateTime.parse("2021-05-10T09:00:01+00:00"))
        then(toAnotherZone(date, ZoneId.of("Europe/Berlin")))
            .isEqualTo(ZonedDateTime.parse("2021-05-10T11:00:01+02:00"))

    }

    @Test
    fun `test convert date with timezone to another timezone`() {

        val date = ZonedDateTime.parse("2021-05-10T11:00:01+02:00")

        then(toAnotherZone(date, ZoneId.of("Europe/London")))
            .isEqualTo(ZonedDateTime.parse("2021-05-10T11:00:01+02:00"))
        then(toAnotherZone(date, ZoneId.of("Asia/Singapore")))
            .isEqualTo(ZonedDateTime.parse("2021-05-10T17:00:01+08:00"))
        then(toAnotherZone(date, ZoneId.of("UTC")))
            .isEqualTo(ZonedDateTime.parse("2021-05-10T09:00:01+00:00"))
        then(toAnotherZone(date, ZoneId.of("Europe/Berlin")))
            .isEqualTo(ZonedDateTime.parse("2021-05-10T11:00:01+02:00"))

    }

    @Test
    fun `test convert date string to timezone date`() {

        val date = "2021-05-10T11:00:01+02:00"

        then(toZonedDateTime(date)).isEqualTo(ZonedDateTime.parse("2021-05-10T11:00:01+02:00"))

    }

    @Test
    fun `test convert ZonedDateTime to string`() {

        val date = ZonedDateTime.parse("2021-05-10T11:00:01+02:00")

        then(toISODateWithOffset(date)).isEqualTo("2021-05-10T11:00:01+02:00")

    }


}
