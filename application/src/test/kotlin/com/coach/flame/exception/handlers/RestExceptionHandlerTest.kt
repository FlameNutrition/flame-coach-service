package com.coach.flame.exception.handlers

import com.coach.flame.exception.RestException
import com.coach.flame.failure.domain.ErrorDetail
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.context.request.WebRequest

@ExtendWith(MockKExtension::class)
class RestExceptionHandlerTest {

    @MockK
    private lateinit var request: WebRequest

    private val restExceptionHandler = RestExceptionHandler(true)

    @Test
    fun `test handler for rest exceptions`() {

        // given
        val restException = RestException("EXCEPTION")

        // when
        val responseEntity = restExceptionHandler.handleRestException(restException, request)

        // then
        then(responseEntity.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        then(responseEntity.headers.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON)
        //FIXME: Add more assertions to check the error details instance
        then(responseEntity.body).isNotEqualTo(ErrorDetail.Builder().throwable(restException).build())

    }

}