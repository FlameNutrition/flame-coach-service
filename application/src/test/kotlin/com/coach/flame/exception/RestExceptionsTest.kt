package com.coach.flame.exception

import com.coach.flame.failure.HttpStatus
import com.coach.flame.failure.Status
import com.coach.flame.failure.domain.ErrorCode
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class RestExceptionsTest {

    @Test
    fun `check status RestException exception`() {

        // given
        val exception = RestException(ErrorCode.CODE_1001, "exception", IllegalArgumentException())

        // when
        val annotationStatus = exception::class.java.getAnnotation(Status::class.java)

        // when & then
        then(annotationStatus).isNotNull
        then(annotationStatus.httpStatus).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)

    }
}
