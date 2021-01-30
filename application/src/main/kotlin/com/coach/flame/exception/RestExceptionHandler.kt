package com.coach.flame.exception

import com.coach.flame.api.response.ErrorDetail
import com.coach.flame.dailyTask.BusinessElementNotFound
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.net.URI

@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(RestExceptionHandler::class.java)
    }

    @ExceptionHandler(Exception::class)
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun handleRootException(ex: Exception, request: WebRequest): ResponseEntity<Any> {

        LOGGER.warn("operation=handleRootException, message='Something unexpected happened'", ex)

        val httpStatus = HttpStatus.INTERNAL_SERVER_ERROR

        //FIXME: This can be improved following this guide
        //https://blog.codecentric.de/en/2020/01/rfc-7807-problem-details-with-spring-boot-and-jax-rs/
        val errorDetail = ErrorDetail.Builder()
            .title("Generic error.")
            .status(httpStatus.value())
            .detail("This was a unexpected error. Please contact the system administration.")
            .instance(URI.create((request as ServletWebRequest).request.requestURI))
            .debug(ex.localizedMessage)
            .build()

        return ResponseEntity
            .status(httpStatus)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(errorDetail)
    }

    @ExceptionHandler(BusinessElementNotFound::class)
    @Order(1)
    fun handleBusinessElementNotFound(ex: BusinessElementNotFound, request: WebRequest): ResponseEntity<Any> {

        LOGGER.warn("operation=handleBusinessElementNotFound, message='Business element not found'", ex)

        val httpStatus = HttpStatus.NOT_FOUND

        val errorDetail = ErrorDetail.Builder()
            .title("Business element not found.")
            .status(httpStatus.value())
            .detail("The business element requested was not found.")
            .instance(URI.create((request as ServletWebRequest).request.requestURI))
            .debug(ex.localizedMessage)
            .build()

        return ResponseEntity
            .status(httpStatus)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(errorDetail)

    }

}