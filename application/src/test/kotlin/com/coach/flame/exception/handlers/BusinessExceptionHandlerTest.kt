package com.coach.flame.exception.handlers

import com.coach.flame.dailyTask.DailyTaskNotFound
import com.coach.flame.exception.handlers.BusinessExceptionHandler
import com.coach.flame.failure.domain.ErrorDetail
import com.coach.flame.failure.exception.BusinessException
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.context.request.WebRequest

@ExtendWith(MockKExtension::class)
class BusinessExceptionHandlerTest {

    @MockK
    private lateinit var request: WebRequest

    private val businessExceptionHandler = BusinessExceptionHandler()

    @Test
    fun `test handler for business exceptions`() {

        // given
        val exception = BusinessException("BUSINESS EXCEPTION")

        // when
        val responseEntity = businessExceptionHandler.handleBusinessExceptions(exception, request)

        // then
        then(responseEntity.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        then(responseEntity.headers.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON)
        //FIXME: Add more assertions to check the error details instance
        then(responseEntity.body).isNotEqualTo(ErrorDetail.Builder().throwable(exception).build())

    }

    @Test
    fun `test handler for a specific business exceptions`() {

        // given
        val exception = DailyTaskNotFound("DAILY TASK NOT FOUND")

        // when
        val responseEntity = businessExceptionHandler.handleSpecificBusinessExceptions(exception, request)

        // then
        then(responseEntity.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        then(responseEntity.headers.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON)
        //FIXME: Add more assertions to check the error details instance
        then(responseEntity.body).isNotEqualTo(ErrorDetail.Builder().throwable(exception).build())

    }

}