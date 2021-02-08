package com.coach.flame.dailyTask

import com.coach.flame.failure.HttpStatus
import com.coach.flame.failure.Status
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import java.lang.RuntimeException

class DailyTaskExceptionTest {

    @Test
    fun `check status ClientNotFound exception`() {

        // given
        val clientNotFound = ClientNotFound("ex1", RuntimeException("EXCEPTION"))

        // when
        val annotationStatus = clientNotFound::class.java.getAnnotation(Status::class.java)

        // when & then
        then(annotationStatus).isNotNull
        then(annotationStatus.httpStatus).isEqualTo(HttpStatus.NOT_FOUND)

    }

    @Test
    fun `check status DailyTaskNotFound exception`() {

        // given
        val dailyTaskNotFound = DailyTaskNotFound("ex1", RuntimeException("EXCEPTION"))

        // when
        val annotationStatus = dailyTaskNotFound::class.java.getAnnotation(Status::class.java)

        // when & then
        then(annotationStatus).isNotNull
        then(annotationStatus.httpStatus).isEqualTo(HttpStatus.NOT_FOUND)

    }

    @Test
    fun `check status DailyTaskMissingSave exception`() {

        // given
        val dailyTaskMissingSave = DailyTaskMissingSave("ex1", RuntimeException("EXCEPTION"))

        // when
        val annotationStatus = dailyTaskMissingSave::class.java.getAnnotation(Status::class.java)

        // when & then
        then(annotationStatus).isNotNull
        then(annotationStatus.httpStatus).isEqualTo(HttpStatus.BAD_REQUEST)

    }

    @Test
    fun `check status DailyTaskMissingDelete exception`() {

        // given
        val dailyTaskMissingDelete = DailyTaskMissingDelete("ex1")

        // when
        val annotationStatus = dailyTaskMissingDelete::class.java.getAnnotation(Status::class.java)

        // when & then
        then(annotationStatus).isNotNull
        then(annotationStatus.httpStatus).isEqualTo(HttpStatus.BAD_REQUEST)

    }


}