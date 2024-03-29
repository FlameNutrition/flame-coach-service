package com.coach.flame.aspect

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.Appender
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.Logger
import org.aspectj.lang.JoinPoint
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ResponseLoggingAspectTest {

    private val logger: Logger = LogManager.getLogger(ResponseLoggingAspect::class.java) as Logger

    private val appender = mockk<Appender>()

    private val joinPoint = mockk<JoinPoint>()

    private val captorLoggingEvent = slot<LogEvent>()

    private val responseLoggingAspect = ResponseLoggingAspect()

    @BeforeEach
    internal fun setUp() {
        every { appender.name } returns "loggingName"
        every { appender.isStarted } returns true
        every { appender.isStopped } returns false

        logger.addAppender(appender)
        logger.level = Level.INFO
    }

    @AfterEach
    internal fun tearDown() {
        logger.removeAppender(appender)
    }

    @Test
    fun `test logging response`() {

        every { joinPoint.signature.name } returns "methodName"
        every { appender.append(capture(captorLoggingEvent)) }

        responseLoggingAspect.responseLogging(joinPoint, "Hello")

        assertEquals(Level.INFO, captorLoggingEvent.captured.level)
        assertEquals("opr='{}', msg='Response', response='{}'", captorLoggingEvent.captured.message.format)
        assertEquals("opr='methodName', msg='Response', response='Hello'",
            captorLoggingEvent.captured.message.formattedMessage
        )

    }

    @Test
    fun `test logging response when exception occurred`() {

        every { joinPoint.signature.name } returns "methodName"
        every { appender.append(capture(captorLoggingEvent)) }

        responseLoggingAspect.responseLogging(joinPoint, IllegalArgumentException("Invalid arg"))

        assertEquals(Level.INFO, captorLoggingEvent.captured.level)
        assertEquals("opr='{}', msg='Response', response='{}'", captorLoggingEvent.captured.message.format)
        assertEquals("opr='methodName', msg='Response', response='java.lang.IllegalArgumentException: Invalid arg'",
            captorLoggingEvent.captured.message.formattedMessage)

    }

}
