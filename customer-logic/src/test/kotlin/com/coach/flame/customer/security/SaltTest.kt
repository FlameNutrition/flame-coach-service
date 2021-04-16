package com.coach.flame.customer.security

import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class SaltTest {

    private val subject = Salt(20)

    @Test
    fun `test illegal length 0`(){

        val exception = catchThrowable { Salt(0) }

        then(exception)
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("length must be > 1")

    }

    @Test
    fun `test illegal length -1`(){

        val exception = catchThrowable { Salt(-1) }

        then(exception)
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("length must be > 1")

    }

    @Test
    fun `test get valid salt`(){

        val salt = subject.generate()

        then(salt).isBase64
        then(salt).isNotNull
        then(salt).isNotEmpty
    }
}
