package com.coach.flame.exception.handlers

import com.coach.flame.exception.RestException
import com.coach.flame.exception.RestInvalidRequestException
import com.coach.flame.failure.domain.ErrorCode
import com.coach.flame.failure.domain.ErrorDetail
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.context.request.WebRequest

@ExtendWith(MockKExtension::class)
class RestExceptionHandlerTest {

    @MockK
    private lateinit var request: WebRequest

    private val restExceptionHandler = RestExceptionHandler(true)

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test handler for rest exceptions`() {

        // given
        val restException = RestException(ErrorCode.CODE_1001, "EXCEPTION", IllegalArgumentException())

        // when
        val responseEntity = restExceptionHandler.handleRestException(restException, request)

        // then
        then(responseEntity.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        then(responseEntity.headers.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON)
        //FIXME: Add more assertions to check the error details instance
        then(responseEntity.body).isNotEqualTo(ErrorDetail.Builder().throwable(restException).build())

    }

    @Test
    fun `test handler for missing parameters exceptions`() {

        // given
        val restException = MissingServletRequestParameterException("paramTest", "UUID")

        // when
        val responseEntity = restExceptionHandler.handleMissingParameterException(restException, request)

        // then
        then(responseEntity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        then(responseEntity.headers.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON)
        //FIXME: Add more assertions to check the error details instance
        then(responseEntity.body).isNotEqualTo(ErrorDetail.Builder().throwable(restException).build())

    }

    @Test
    fun `test handler for bind exceptions`() {

        // given
        val mockException = mockk<BindException>(relaxed = true)

        every { mockException.fieldError } returns FieldError("obj", "field", "default message")

        // when
        val responseEntity = restExceptionHandler.handleBindException(mockException, request)

        // then
        val expectedException = RestInvalidRequestException("param field: default message", mockException)

        then(responseEntity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        then(responseEntity.headers.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON)
        //FIXME: Add more assertions to check the error details instance
        then(responseEntity.body).isNotEqualTo(ErrorDetail.Builder().throwable(expectedException).build())

    }

}
