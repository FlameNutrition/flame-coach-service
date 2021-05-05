package com.coach.flame.dailyTask

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

class DailyTaskExceptionTest {

    @Test
    fun `check status DailyTaskNotFound exception`() {

        // given
        val dailyTaskNotFound = DailyTaskNotFoundException("ex1", RuntimeException("EXCEPTION"))

        // when
        val annotationStatus = dailyTaskNotFound::class.java.getAnnotation(Status::class.java)

        // when & then
        then(annotationStatus).isNotNull
        then(annotationStatus.httpStatus).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `check status DailyTaskMissingDelete exception`() {

        // given
        val dailyTaskMissingDelete = DailyTaskMissingDeleteException("ex1")

        // when
        val annotationStatus = dailyTaskMissingDelete::class.java.getAnnotation(Status::class.java)

        // when & then
        then(annotationStatus).isNotNull
        then(annotationStatus.httpStatus).isEqualTo(HttpStatus.BAD_REQUEST)

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

            val customerNotFoundException = Arguments.of(
                "CustomerNotFoundException",
                CustomerNotFoundException("EXCEPTION"),
                ErrorCode.CODE_2001
            )

            val dailyTaskNotFoundException = Arguments.of(
                "DailyTaskNotFoundException",
                DailyTaskNotFoundException("EXCEPTION"),
                ErrorCode.CODE_4001
            )

            val dailyTaskMissingDeleteException = Arguments.of(
                "DailyTaskMissingDeleteException",
                DailyTaskMissingDeleteException("EXCEPTION"),
                ErrorCode.CODE_4002
            )

            return Stream.of(
                customerNotFoundException,
                dailyTaskNotFoundException,
                dailyTaskMissingDeleteException
            )
        }
    }

    // endregion


}
