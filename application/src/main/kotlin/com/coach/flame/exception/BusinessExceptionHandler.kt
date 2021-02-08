package com.coach.flame.exception

import com.coach.flame.dailyTask.ClientNotFound
import com.coach.flame.dailyTask.DailyTaskMissingDelete
import com.coach.flame.dailyTask.DailyTaskMissingSave
import com.coach.flame.dailyTask.DailyTaskNotFound
import com.coach.flame.failure.domain.ErrorDetail
import com.coach.flame.failure.exception.BusinessException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
@Order(1)
class BusinessExceptionHandler {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(BusinessExceptionHandler::class.java)
    }

    @ExceptionHandler(
        value = [
            BusinessException::class,
        ]
    )
    fun handleBusinessExceptions(ex: BusinessException, request: WebRequest): ResponseEntity<Any> {

        LOGGER.warn("operation=handleBusinessExceptions, exception='{}'", ex::class.java.simpleName, ex)

        val errorDetail = ErrorDetail.Builder()
            .throwable(ex)
            .build()

        return ResponseEntity
            .status(HttpStatus.valueOf(errorDetail.status))
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(errorDetail)

    }

    @ExceptionHandler(
        value = [
            DailyTaskNotFound::class,
            DailyTaskMissingSave::class,
            ClientNotFound::class,
            DailyTaskMissingDelete::class,
        ]
    )
    fun handleSpecificBusinessExceptions(ex: BusinessException, request: WebRequest): ResponseEntity<Any> {

        LOGGER.warn("operation=handleSpecificBusinessExceptions, exception='{}'", ex::class.java.simpleName, ex)

        val errorDetail = ErrorDetail.Builder()
            .throwable(ex)
            .build()

        return ResponseEntity
            .status(HttpStatus.valueOf(errorDetail.status))
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(errorDetail)

    }

}