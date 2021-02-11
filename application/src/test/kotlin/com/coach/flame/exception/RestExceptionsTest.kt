package com.coach.flame.exception

import com.coach.flame.failure.HttpStatus
import com.coach.flame.failure.Status
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import java.lang.RuntimeException

class RestExceptionsTest {

    @Test
    fun `check status RestException exception`() {

        // given
        val exception = RestException(RuntimeException("EXCEPTION"))

        // when
        val annotationStatus = exception::class.java.getAnnotation(Status::class.java)

        // when & then
        then(annotationStatus).isNotNull
        then(annotationStatus.httpStatus).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)

    }

    @Test
    fun `check status RestInvalidRequest exception`() {

        // given
        val invalidRequest = RestInvalidRequest(RuntimeException("EXCEPTION"))

        // when
        val annotationStatus = invalidRequest::class.java.getAnnotation(Status::class.java)

        // when & then
        then(annotationStatus).isNotNull
        then(annotationStatus.httpStatus).isEqualTo(HttpStatus.BAD_REQUEST)

    }

}