package com.coach.flame.exception.handlers

import com.coach.flame.exception.handlers.DefaultExceptionHandler
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
class DefaultExceptionHandlerTest {

    @MockK
    private lateinit var request: WebRequest

    private val defaultExceptionHandler = DefaultExceptionHandler()

    @Test
    fun `test handler for business exceptions`() {

        // given
        val exception = RuntimeException("RUNTIME EXCEPTION")

        // when
        val responseEntity = defaultExceptionHandler.handleRootException(exception, request)

        // then
        then(responseEntity.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        then(responseEntity.headers.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON)
        //FIXME: Add more assertions to check the error details instance
        then(responseEntity.body).isNotEqualTo(ErrorDetail.Builder().throwable(exception).build())

    }

}