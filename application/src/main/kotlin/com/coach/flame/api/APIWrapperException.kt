package com.coach.flame.api

import com.coach.flame.exception.RestException
import com.coach.flame.exception.RestInvalidRequestException
import com.coach.flame.failure.domain.ErrorCode
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object APIWrapperException {

    private val LOGGER: Logger = LoggerFactory.getLogger(APIWrapperException::class.java)

    /**
     * Higher-order function to execute the request, if request fails system will trigger an exception
     *
     * @param f function to invoke
     *
     * @exception IllegalArgumentException when request is invalid
     * @exception IllegalStateException when missing a mandatory parameter
     * @exception Exception when happen another exception not mentioned above
     *
     * @return the result of function
     */
    fun <R> executeRequest(f: () -> R): R {
        try {
            return f.invoke()
        } catch (ex: Exception) {
            when (ex) {
                is IllegalArgumentException -> {
                    LOGGER.warn("opr='executeRequest', msg='Invalid request'", ex)
                    throw RestInvalidRequestException(ex.localizedMessage, ex)
                }
                is IllegalStateException -> {
                    LOGGER.warn("opr='executeRequest', msg='Please check following problem'", ex)
                    throw RestException(ErrorCode.CODE_1000, ex.localizedMessage, ex)
                }
                else -> {
                    throw ex
                }
            }
        }
    }

}
