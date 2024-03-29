package com.coach.flame.exception.handlers

import com.coach.flame.failure.domain.ErrorCode
import com.coach.flame.failure.domain.ErrorDetail
import com.coach.flame.failure.exception.BusinessException
import io.mockk.clearAllMocks
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.context.request.WebRequest

@ExtendWith(MockKExtension::class)
class BusinessExceptionHandlerTest {

    @MockK
    private lateinit var request: WebRequest

    private val businessExceptionHandler = BusinessExceptionHandler(true)

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test handler for business exceptions`() {

        // given
        val exception = BusinessException(ErrorCode.CODE_1001, "BUSINESS EXCEPTION")

        // when
        val responseEntity = businessExceptionHandler.handleBusinessExceptions(exception, request)

        // then
        then(responseEntity.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        then(responseEntity.headers.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON)
        //FIXME: Add more assertions to check the error details instance
        then(responseEntity.body).isNotEqualTo(ErrorDetail.Builder().throwable(exception).build())

    }

}
