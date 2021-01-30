package com.coach.flame.aspect

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class RequestLoggingAspect {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(RequestLoggingAspect::class.java)
    }

    @Before("@annotation(com.coach.flame.aspect.LoggingRequest)")
    fun requestLogging(joinPoint: JoinPoint) {

        var listOfArgs: List<Any> = listOf(joinPoint.signature.name)
        var loggingFormatArgs = " msg='Request'"

        if (joinPoint.args != null && joinPoint.args.isNotEmpty()) {
            val args = joinPoint.args.toList()
            val argsNames = (joinPoint.signature as MethodSignature).parameterNames
            val argsLoggingFormat = argsNames.joinToString { argName -> "$argName={}" }

            listOfArgs = listOf(listOfArgs, args).flatten()

            loggingFormatArgs = "$loggingFormatArgs, $argsLoggingFormat"
        } else {
            loggingFormatArgs = "$loggingFormatArgs, request='N/A'"
        }

        LOGGER.info("operation='{}',$loggingFormatArgs", *listOfArgs.toTypedArray())

    }

}