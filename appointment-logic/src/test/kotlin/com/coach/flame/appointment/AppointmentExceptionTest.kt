package com.coach.flame.appointment

import com.coach.flame.failure.HttpStatus
import com.coach.flame.failure.Status
import com.coach.flame.failure.domain.ErrorCode
import com.coach.flame.failure.exception.BusinessException
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class AppointmentExceptionTest {

    @Test
    fun `check status AppointmentNotFound exception`() {

        // given
        val exception = AppointmentNotFoundException("ex1", RuntimeException("EXCEPTION"))

        // when
        val annotationStatus = exception::class.java.getAnnotation(Status::class.java)

        // when & then
        then(annotationStatus).isNotNull
        then(annotationStatus.httpStatus).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `check status AppointmentMissingDelete exception`() {

        // given
        val exception = AppointmentMissingDeleteException("ex1")

        // when
        val annotationStatus = exception::class.java.getAnnotation(Status::class.java)

        // when & then
        then(annotationStatus).isNotNull
        then(annotationStatus.httpStatus).isEqualTo(HttpStatus.NOT_FOUND)

    }

    @ParameterizedTest(name = "[{index}] - {0} exception should has {2} error code")
    @MethodSource("errorCodeCheckParameters")
    fun `test errorCode exceptions`(exceptionName: String, exception: BusinessException, errorCodeExpected: ErrorCode) {

        then(exception.errorCode).isEqualTo(errorCodeExpected)

    }

    // region Parameters

    companion object {
        @JvmStatic
        fun errorCodeCheckParameters(): Stream<Arguments> {

            val appointmentNotFoundException = Arguments.of(
                "DailyTaskNotFoundException",
                AppointmentNotFoundException("EXCEPTION"),
                ErrorCode.CODE_2101
            )

            val appointmentMissingDeleteException = Arguments.of(
                "DailyTaskMissingDeleteException",
                AppointmentMissingDeleteException("EXCEPTION"),
                ErrorCode.CODE_2102
            )

            return Stream.of(
                appointmentNotFoundException,
                appointmentMissingDeleteException
            )
        }
    }

    // endregion


}
