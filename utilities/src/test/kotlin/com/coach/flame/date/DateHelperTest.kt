package com.coach.flame.date

import org.assertj.core.api.BDDAssertions.thenExceptionOfType
import org.assertj.core.api.BDDAssertions.thenNoException
import org.junit.jupiter.api.Test

class DateHelperTest {

    @Test
    fun `test convert string to local date`() {

        thenNoException().isThrownBy { stringToDate("2020-12-05") }

    }

    @Test
    fun `test convert invalid string to local date`() {
        
        thenExceptionOfType(IllegalArgumentException::class.java).isThrownBy { stringToDate("05-12-2020") }
        thenExceptionOfType(IllegalArgumentException::class.java).isThrownBy { stringToDate("20") }

    }
}