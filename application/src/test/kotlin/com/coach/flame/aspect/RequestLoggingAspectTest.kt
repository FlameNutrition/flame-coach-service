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
import org.aspectj.lang.reflect.MethodSignature
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

class RequestLoggingAspectTest {

    private val logger: Logger = LogManager.getLogger(RequestLoggingAspect::class.java) as Logger

    private val appender = mockk<Appender>()

    private val joinPoint = mockk<JoinPoint>()

    private val methodSignature = mockk<MethodSignature>()

    private val captorLoggingEvent = slot<LogEvent>()

    private val requestLoggingAspect = RequestLoggingAspect()

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
    fun `test logging request without parameters`() {

        every { joinPoint.signature.name } returns "methodName"
        every { joinPoint.args } returns arrayOf()
        every { appender.append(capture(captorLoggingEvent)) }

        requestLoggingAspect.requestLogging(joinPoint)

        assertEquals(Level.INFO, captorLoggingEvent.captured.level)
        assertEquals("operation='{}', msg='Request', request='N/A'", captorLoggingEvent.captured.message.format)
        assertEquals(
            "operation='methodName', msg='Request', request='N/A'",
            captorLoggingEvent.captured.message.formattedMessage
        )

    }

    @Test
    fun `test logging request with one param`() {

        every { joinPoint.args } returns arrayOf(10)
        every { joinPoint.signature } returns methodSignature
        every { methodSignature.name } returns "methodName"
        every { methodSignature.parameterNames } returns arrayOf("arg1")
        every { appender.append(capture(captorLoggingEvent)) }

        requestLoggingAspect.requestLogging(joinPoint)

        assertEquals(Level.INFO, captorLoggingEvent.captured.level)
        assertEquals("operation='{}', msg='Request', arg1={}", captorLoggingEvent.captured.message.format)
        assertEquals(
            "operation='methodName', msg='Request', arg1=10",
            captorLoggingEvent.captured.message.formattedMessage
        )

    }

    @Test
    fun `test logging request with multiple param`() {

        every { joinPoint.args } returns arrayOf(10, 30, "Hello Test")
        every { joinPoint.signature } returns methodSignature
        every { methodSignature.name } returns "methodName"
        every { methodSignature.parameterNames } returns arrayOf("request", "arg2", "stringValue")
        every { appender.append(capture(captorLoggingEvent)) }

        requestLoggingAspect.requestLogging(joinPoint)

        assertEquals(Level.INFO, captorLoggingEvent.captured.level)
        assertEquals(
            "operation='{}', msg='Request', request={}, arg2={}, stringValue={}",
            captorLoggingEvent.captured.message.format
        )
        assertEquals(
            "operation='methodName', msg='Request', request=10, arg2=30, stringValue=Hello Test",
            captorLoggingEvent.captured.message.formattedMessage
        )

    }

}