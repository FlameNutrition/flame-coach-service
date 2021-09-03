package com.coach.flame.domain

import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import java.time.LocalDate

class DateIntervalDtoTest {

    @Test
    fun `test is one date is between the date interval`() {

        val interval = DateIntervalDto(
            LocalDate.of(2021, 5, 1),
            LocalDate.of(2021, 5, 31))

        then(interval.isBetweenInterval(LocalDate.of(2021, 4, 30))).isFalse
        then(interval.isBetweenInterval(LocalDate.of(2021, 5, 1))).isTrue
        then(interval.isBetweenInterval(LocalDate.of(2021, 5, 20))).isTrue
        then(interval.isBetweenInterval(LocalDate.of(2021, 5, 31))).isTrue
        then(interval.isBetweenInterval(LocalDate.of(2021, 6, 1))).isFalse
        then(interval.isBetweenInterval(LocalDate.of(2021, 10, 1))).isFalse

    }

    @Test
    fun `test when from is after to in date interval`() {

        val from = LocalDate.of(2021, 6, 1)
        val to = LocalDate.of(2021, 5, 31)

        val exception = catchThrowable {
            DateIntervalDto(from, to)
        }

        then(exception).isInstanceOf(IllegalStateException::class.java)
        then(exception).hasMessage("from: $from can not be after to: $to")

    }

    @Test
    fun `test when from is equal to in date interval`() {

        val from = LocalDate.of(2021, 5, 31)
        val to = LocalDate.of(2021, 5, 31)

        val exception = catchThrowable {
            DateIntervalDto(from, to)
        }

        then(exception).isInstanceOf(IllegalStateException::class.java)
        then(exception).hasMessage("from: $from can not be equal to: $to")

    }

}
