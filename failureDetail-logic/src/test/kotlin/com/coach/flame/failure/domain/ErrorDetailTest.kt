package com.coach.flame.failure.domain

import com.coach.flame.failure.HttpStatus
import com.coach.flame.failure.exception.BusinessException
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import java.net.URI

class ErrorDetailTest {

    @Test
    fun `test error detail domain with specific business exception`() {

        val arithmeticException = ArithmeticException()

        val businessException = BusinessException("This is the message", arithmeticException)

        val errorDetail = ErrorDetail.Builder()
            .throwable(businessException)
            .build()

        assertThat(errorDetail.type)
            .isEqualTo(URI.create("https://flame-coach/apidocs/com/coach/flame/failure/exception/DailyTaskNotFound.html"))
        assertThat(errorDetail.status).isEqualTo(HttpStatus.NOT_FOUND.value)
        assertThat(errorDetail.title).isEqualTo("DailyTaskNotFound")
        assertThat(errorDetail.detail).isEqualTo("This is the message")
        assertThat(errorDetail.instance.toString()).startsWith("urn:uuid:")
        assertThat(errorDetail.debug).contains("\n")

    }

}