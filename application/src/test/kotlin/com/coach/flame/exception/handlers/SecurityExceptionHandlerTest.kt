package com.coach.flame.exception.handlers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.clearAllMocks
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.core.AuthenticationException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@ExtendWith(MockKExtension::class)
class SecurityExceptionHandlerTest {

    @MockK(relaxed = true)
    private lateinit var request: HttpServletRequest

    @MockK(relaxed = true)
    private lateinit var response: HttpServletResponse

    @MockK(relaxed = true)
    private lateinit var exception: AuthenticationException

    private val securityExceptionHandler = SecurityExceptionHandler(true)

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test handler for security exceptions`() {

        // when
        securityExceptionHandler.commence(request, response, exception)

        // then
        verify(exactly = 1) { response.contentType = "application/json" }
        verify(exactly = 1) { response.status = HttpServletResponse.SC_UNAUTHORIZED }

    }

}
