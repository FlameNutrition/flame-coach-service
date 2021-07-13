package com.coach.flame.exception.handlers

import com.coach.flame.exception.RestException
import com.coach.flame.exception.RestInvalidRequestException
import com.coach.flame.failure.domain.ErrorDetail
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
@Order(2)
class RestExceptionHandler(
    @Value(value = "\${flamecoach.rest.debug.enable}") private val restDebugEnable: Boolean,
) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(RestExceptionHandler::class.java)
    }

    @ExceptionHandler(
        value = [
            RestException::class,
            RestInvalidRequestException::class
        ]
    )
    fun handleRestException(ex: RestException, request: WebRequest): ResponseEntity<Any> {

        val errorDetail = ErrorDetail.Builder()
            .withEnableDebug(restDebugEnable)
            .throwable(ex)
            .build()

        return ResponseEntity
            .status(HttpStatus.valueOf(errorDetail.status))
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(errorDetail)
    }

    @ExceptionHandler(
        value = [
            MissingServletRequestParameterException::class
        ]
    )
    fun handleMissingParameterException(
        ex: MissingServletRequestParameterException,
        request: WebRequest,
    ): ResponseEntity<Any> {

        val restException = RestInvalidRequestException(ex.localizedMessage, ex)

        val errorDetail = ErrorDetail.Builder()
            .withEnableDebug(restDebugEnable)
            .throwable(restException)
            .build()

        return ResponseEntity
            .status(HttpStatus.valueOf(errorDetail.status))
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(errorDetail)
    }

}
