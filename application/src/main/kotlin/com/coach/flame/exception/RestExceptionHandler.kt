package com.coach.flame.exception

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
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
@Order(2)
class RestExceptionHandler : ResponseEntityExceptionHandler() {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(RestExceptionHandler::class.java)
    }

    @ExceptionHandler(
        value = [
            RestInvalidRequest::class,
        ]
    )
    fun handleRestException(ex: RestInvalidRequest, request: WebRequest): ResponseEntity<Any> {

        LOGGER.warn("operation=handleRestException, message='Something unexpected happened'", ex)

        val errorDetail = ErrorDetail.Builder()
            .throwable(ex)
            .build()

        return ResponseEntity
            .status(HttpStatus.valueOf(errorDetail.status))
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(errorDetail)
    }

}