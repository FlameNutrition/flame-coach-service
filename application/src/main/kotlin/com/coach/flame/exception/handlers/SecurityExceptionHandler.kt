package com.coach.flame.exception.handlers

import com.coach.flame.exception.RestAuthenticationException
import com.coach.flame.failure.domain.ErrorDetail
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class SecurityExceptionHandler(
    @Value(value = "\${flamecoach.rest.debug.enable}") private val restDebugEnable: Boolean,
) : AuthenticationEntryPoint {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(SecurityExceptionHandler::class.java)
    }

    override fun commence(
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse,
        exception: AuthenticationException,
    ) {

        val errorDetail = ErrorDetail.Builder()
            .withEnableDebug(restDebugEnable)
            .throwable(RestAuthenticationException("Full authentication is required to access this resource", exception))
            .build()

        httpResponse.apply {
            contentType = "application/json"
            status = HttpServletResponse.SC_UNAUTHORIZED
            outputStream.println(jacksonObjectMapper().writeValueAsString(errorDetail))
        }

    }

}
