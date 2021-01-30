package com.coach.flame.aspect

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class ResponseLoggingAspect {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(ResponseLoggingAspect::class.java)
    }

    @AfterReturning(value = "@annotation(com.coach.flame.aspect.LoggingRequest)", returning = "objReturn")
    fun responseLogging(joinPoint: JoinPoint, objReturn: Any) {

        val opr = joinPoint.signature.name
        LOGGER.info("operation='{}', msg='Response', response='{}'", opr, objReturn)

    }

}