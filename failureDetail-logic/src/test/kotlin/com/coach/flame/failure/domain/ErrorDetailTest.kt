package com.coach.flame.failure.domain

import com.coach.flame.failure.HttpStatus
import com.coach.flame.failure.Status
import com.coach.flame.failure.exception.BusinessException
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import java.net.URI

class ErrorDetailTest {

    @Status(HttpStatus.NOT_FOUND)
    private class TestException(message: String) : BusinessException(message)

    @Test
    fun `test error detail domain with specific business exception`() {

        val exception = TestException("This is the message")

        val errorDetail = ErrorDetail.Builder()
            .withEnableDebug(true)
            .throwable(exception)
            .build()

        then(errorDetail.type)
            .isEqualTo(URI.create("https://flame-coach/apidocs/com/coach/flame/failure/domain/ErrorDetailTest.TestException.html"))
        then(errorDetail.status).isEqualTo(HttpStatus.NOT_FOUND.value)
        then(errorDetail.title).isEqualTo("TestException")
        then(errorDetail.detail).isEqualTo("This is the message")
        then(errorDetail.instance.toString()).startsWith("urn:uuid:")
        then(errorDetail.debug).contains("\n")

    }

    @Test
    fun `test error detail domain with generic business exception`() {

        val arithmeticException = ArithmeticException()

        val businessException = BusinessException("This is the message", arithmeticException)

        val errorDetail = ErrorDetail.Builder()
            .withEnableDebug(true)
            .throwable(businessException)
            .build()

        then(errorDetail.type)
            .isEqualTo(URI.create("https://flame-coach/apidocs/com/coach/flame/failure/exception/BusinessException.html"))
        then(errorDetail.status).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value)
        then(errorDetail.title).isEqualTo("BusinessException")
        then(errorDetail.detail).isEqualTo("This is the message")
        then(errorDetail.instance.toString()).startsWith("urn:uuid:")
        then(errorDetail.debug).contains("\n")

    }

    @Test
    fun `test error detail hashCode and equals`() {

        // given
        val arithmeticException = ArithmeticException()
        val businessException = BusinessException("This is the message", arithmeticException)

        // when
        val errorDetail = ErrorDetail.Builder()
            .throwable(businessException)
            .build()
        val errorDetailOther = ErrorDetail.Builder()
            .throwable(businessException)
            .build()

        // then
        then(errorDetail.hashCode()).isNotEqualTo(errorDetailOther.hashCode())
        then(errorDetail == errorDetailOther).isFalse
        then(errorDetail == errorDetail).isTrue
        then(errorDetail.equals("OTHER")).isFalse
        then(errorDetail.equals(null)).isFalse

    }

    @Test
    fun `test error detail domain without debug enable`() {

        val arithmeticException = ArithmeticException()

        val businessException = BusinessException("This is the message", arithmeticException)

        val errorDetail = ErrorDetail.Builder()
            .throwable(businessException)
            .build()

        then(errorDetail.type)
            .isEqualTo(URI.create("https://flame-coach/apidocs/com/coach/flame/failure/exception/BusinessException.html"))
        then(errorDetail.status).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value)
        then(errorDetail.title).isEqualTo("BusinessException")
        then(errorDetail.detail).isEqualTo("This is the message")
        then(errorDetail.instance.toString()).startsWith("urn:uuid:")
        then(errorDetail.debug).isEmpty()

    }

}