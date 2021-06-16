package com.coach.flame.exception.handlers

import com.coach.flame.exception.InternalServerException
import com.coach.flame.failure.domain.ErrorCode
import com.coach.flame.failure.domain.ErrorDetail
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
class DefaultExceptionHandler(
    @Value(value = "\${flamecoach.rest.debug.enable}") private val restDebugEnable: Boolean,
) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(DefaultExceptionHandler::class.java)
    }

    @ExceptionHandler(Exception::class)
    fun handleRootException(ex: Exception, request: WebRequest): ResponseEntity<Any> {

        LOGGER.error("opr=handleRootException, msg='Unexpected error happened. This will be wrapped into InternalServerException'", ex)

        val internalException = InternalServerException(ErrorCode.CODE_9999,
            "This is an internal problem, please contact the admin system.",
            ex)

        val errorDetail = ErrorDetail.Builder()
            .withEnableDebug(restDebugEnable)
            .throwable(internalException)
            .build()

        return ResponseEntity
            .status(HttpStatus.valueOf(errorDetail.status))
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(errorDetail)
    }

}
