package com.coach.flame.customer.security

import com.coach.flame.customer.SecurityException
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class HashPasswordTest {

    private val subject = HashPassword("PBKDF2WithHmacSHA512", 65536, 512)

    @Test
    fun `test illegal algorithm`() {

        val exception = catchThrowable { HashPassword("", 20, 20) }

        then(exception)
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("algorithm can not be empty")

    }

    @Test
    fun `test illegal iterations`() {

        val exception = catchThrowable { HashPassword("PBKDF2WithHmacSHA512", 0, 20) }

        then(exception)
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("iterations must be > 0")

    }

    @Test
    fun `test illegal lengthKey`() {

        val exception = catchThrowable { HashPassword("PBKDF2WithHmacSHA512", 20, 0) }

        then(exception)
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("lengthKey must be > 0")

    }


    @Test
    fun `test get valid hash password`() {

        val hashingPassword = subject.generate("TEST_1234", "f1nd1ngn3m0")

        then(hashingPassword).isBase64
        then(hashingPassword).isNotNull
        then(hashingPassword).isNotEmpty
    }

    @Test
    fun `test use invalid algorithm type`() {

        val hashPassword = HashPassword("INVALID", 65536, 512)

        val exception = catchThrowable { hashPassword.generate("TEST_1234", "f1nd1ngn3m0") }

        then(exception)
            .isInstanceOf(SecurityException::class.java)
            .hasMessageContaining("Something happened when trying to encrypt the password.")
    }

    @Test
    fun `test verify when hash password is equal against a plain text password`(){

        val hashingPassword = subject.generate("TEST_1234", "f1nd1ngn3m0")

        then(subject.verify("TEST_1234", hashingPassword, "f1nd1ngn3m0")).isTrue

    }

    @Test
    fun `test verify when hash password is not equal against a plain text password`(){

        val hashingPassword = subject.generate("TEST_12345555", "f1nd1ngn3m0")

        then(subject.verify("TEST_1234", hashingPassword, "f1nd1ngn3m0")).isFalse

    }
}
