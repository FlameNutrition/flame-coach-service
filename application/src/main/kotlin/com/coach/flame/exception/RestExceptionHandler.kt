package com.coach.flame.exception

import com.coach.flame.dailyTask.DailyTaskMissingSave
import com.coach.flame.dailyTask.DailyTaskNotFound
import com.coach.flame.failure.domain.ErrorDetail
import com.coach.flame.failure.exception.BusinessException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(RestExceptionHandler::class.java)
    }

    @ExceptionHandler(Exception::class)
    @Order(Ordered.LOWEST_PRECEDENCE)
    fun handleRootException(ex: Exception, request: WebRequest): ResponseEntity<Any> {

        LOGGER.warn("operation=handleRootException, message='Something unexpected happened'", ex)

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
            BusinessException::class,
        ]
    )
    @Order(2)
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
        ]
    )
    @Order(1)
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