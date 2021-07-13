package com.coach.flame.api

import com.coach.flame.exception.RestException
import com.coach.flame.exception.RestInvalidRequestException
import org.assertj.core.api.Assertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class APIWrapperExceptionTest {

    @Test
    fun testRaiseRestInvalidRequestExceptionWhenIsIllegalArgumentException() {

        val exception = catchThrowable {
            APIWrapperException.executeRequest {
                throw IllegalArgumentException("Hey, this is an illegal argument exception")
            }
        }

        then(exception).isInstanceOf(RestInvalidRequestException::class.java)
        then(exception).hasMessageContaining("Hey, this is an illegal argument exception")

    }

    @Test
    fun testRaiseRestExceptionWhenIsIllegalStateException() {

        val exception = catchThrowable {
            APIWrapperException.executeRequest {
                throw IllegalStateException("Hey, this is an illegal state exception")
            }
        }

        then(exception).isInstanceOf(RestException::class.java)
        then(exception).hasMessageContaining("Hey, this is an illegal state exception")

    }

    @Test
    fun testRaiseOtherRuntimeExceptions() {

        val exception = catchThrowable {
            APIWrapperException.executeRequest {
                throw ClassNotFoundException("Hey, this is an random exception")
            }
        }

        then(exception).isInstanceOf(ClassNotFoundException::class.java)
        then(exception).hasMessageContaining("Hey, this is an random exception")

    }

}
