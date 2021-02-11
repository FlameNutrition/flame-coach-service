package com.coach.flame.api

import com.coach.flame.api.request.DailyTaskRequestGenerator
import com.coach.flame.dailyTask.DailyTaskService
import com.coach.flame.dailyTask.domain.DailyTaskDto
import com.coach.flame.dailyTask.domain.DailyTaskDtoGenerator
import com.coach.flame.date.stringToDate
import com.coach.flame.exception.RestException
import com.coach.flame.exception.RestInvalidRequest
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.*
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
class DailyTaskImplTest {

    @MockK
    private lateinit var dailyTaskService: DailyTaskService

    @InjectMockKs
    private lateinit var dailyTaskImpl: DailyTaskImpl

    // region Daily Task [Create]

    @Test
    fun `create multiple Daily Task`() {

        // given
        val taskBuilder = DailyTaskRequestGenerator.Builder().build()
        val listOfTasks = listOf(taskBuilder.nextObject(), taskBuilder.nextObject())

        // when
        val thrown = catchThrowable { dailyTaskImpl.createDailyTasks(listOfTasks) }

        //then
        then(thrown)
            .isInstanceOf(RestException::class.java)
            .hasMessage("This is not supported yet")
    }

    @ParameterizedTest
    @MethodSource("dailyTaskCheckMandatoryParams")
    fun `create Daily Task with mandatory params`(
        clientIdentCreator: String?,
        clientIdentTask: String?,
        date: String?,
        expectedException: Class<Any>,
        expectedMessage: String
    ) {

        // given
        val task = DailyTaskRequestGenerator.Builder()
            .withClientIdentifierCreator { clientIdentCreator }
            .withClientIdentifierTask { clientIdentTask }
            .withDate { date }
            .build().nextObject()

        // when
        val thrown = catchThrowable { dailyTaskImpl.createDailyTask(task) }

        //then
        then(thrown)
            .isInstanceOf(expectedException)
            .hasMessageContaining(expectedMessage)
    }

    @ParameterizedTest
    @MethodSource("dailyTaskCheckInvalidParams")
    fun `create Daily Task with invalid format params`(
        clientIdentCreator: String?,
        clientIdentTask: String?,
        date: String?,
        expectedException: Class<Any>,
        expectedMessage: String
    ) {

        // given
        val task = DailyTaskRequestGenerator.Builder()
            .withClientIdentifierCreator { clientIdentCreator }
            .withClientIdentifierTask { clientIdentTask }
            .withDate { date }
            .build().nextObject()

        // when
        val thrown = catchThrowable { dailyTaskImpl.createDailyTask(task) }

        //then
        then(thrown)
            .isInstanceOf(expectedException)
            .hasMessageContaining(expectedMessage)
    }

    @Test
    fun `create Daily Task when business throw an exception`() {

        // given
        val task = DailyTaskRequestGenerator.Builder().build().nextObject()
        every { dailyTaskService.createDailyTask(any()) } throws RuntimeException("OHH NOOOO!")

        // when
        val thrown = catchThrowable { dailyTaskImpl.createDailyTask(task) }

        //then
        then(thrown)
            .isInstanceOf(RestException::class.java)
            .hasMessageContaining("OHH NOOOO!")
    }

    @Test
    fun `create Daily Task`() {

        // given
        val taskDto = slot<DailyTaskDto>()
        val task = DailyTaskRequestGenerator.Builder().build().nextObject()
        every { dailyTaskService.createDailyTask(capture(taskDto)) } answers { taskDto.captured.identifier }

        // when
        val response = dailyTaskImpl.createDailyTask(task)

        //then
        then(taskDto.captured.identifier).isNotNull
        then(taskDto.captured.name).isEqualTo(task.name)
        then(taskDto.captured.description).isEqualTo(task.description)
        then(taskDto.captured.date).isEqualTo(stringToDate(task.date))
        then(taskDto.captured.ticked).isFalse
        then(taskDto.captured.createdBy!!.identifier.toString()).isEqualTo(task.clientIdentifierCreator)
        then(taskDto.captured.owner!!.identifier.toString()).isEqualTo(task.clientIdentifierTask)
        then(response.error).isNull()
        then(response.dailyTasks).isNotEmpty
        then(response.dailyTasks!!.first().identifier).isEqualTo(taskDto.captured.identifier.toString())

    }

    // endregion

    // region Daily Task [Get]

    @Test
    fun `get Daily Task by client but something happened`() {

        // given
        val clientId = 100L
        every { dailyTaskService.getDailyTasksByClient(clientId) } throws RuntimeException("OHH NOOOO!")

        // when
        val thrown = catchThrowable { dailyTaskImpl.getDailyTasksByClient(clientId) }

        //then
        then(thrown)
            .isInstanceOf(RestException::class.java)
            .hasMessageContaining("OHH NOOOO!")
    }

    @Test
    fun `get Daily Task by taskId but something happened`() {

        // given
        val taskId = 100L
        every { dailyTaskService.getDailyTaskById(taskId) } throws RuntimeException("OHH NOOOO!")

        // when
        val thrown = catchThrowable { dailyTaskImpl.getDailyTaskById(taskId) }

        //then
        then(thrown)
            .isInstanceOf(RestException::class.java)
            .hasMessageContaining("OHH NOOOO!")
    }

    @Test
    fun `get Daily Task by client`() {

        // given
        val clientId = 100L
        val taskBuilder = DailyTaskDtoGenerator.Builder().build()
        val task1 = taskBuilder.nextObject()
        val task2 = taskBuilder.nextObject()
        every { dailyTaskService.getDailyTasksByClient(clientId) } returns setOf(task1, task2)

        // when
        val response = dailyTaskImpl.getDailyTasksByClient(clientId)

        //then
        then(response.error).isNull()
        then(response.dailyTasks).isNotEmpty
        then(response.dailyTasks!!.size).isEqualTo(2)

        val taskResponseT1 = response.dailyTasks!!.find { task1.identifier.toString() == it.identifier }
        then(taskResponseT1?.name).isEqualTo(task1.name)
        then(taskResponseT1?.description).isEqualTo(task1.description)
        then(taskResponseT1?.date).isEqualTo(task1.date.toString())
        then(taskResponseT1?.ticked).isEqualTo(task1.ticked)

    }

    @Test
    fun `get Daily Task by taskId`() {

        // given
        val taskId = 100L
        val taskBuilder = DailyTaskDtoGenerator.Builder().build()
        val task = taskBuilder.nextObject()
        every { dailyTaskService.getDailyTaskById(taskId) } returns task

        // when
        val response = dailyTaskImpl.getDailyTaskById(taskId)

        //then
        then(response.error).isNull()
        then(response.dailyTasks).isNotEmpty
        then(response.dailyTasks!!.size).isEqualTo(1)

        val taskResponse = response.dailyTasks!!.first()
        then(taskResponse.identifier).isEqualTo(task.identifier.toString())
        then(taskResponse.name).isEqualTo(task.name)
        then(taskResponse.description).isEqualTo(task.description)
        then(taskResponse.date).isEqualTo(task.date.toString())
        then(taskResponse.ticked).isEqualTo(task.ticked)

    }

    // endregion

    // region Daily Task [Delete]

    @Test
    fun `delete Daily Task check invalid params`() {

        // given
        val taskUuid = "INVALID"

        // when
        val thrown = catchThrowable { dailyTaskImpl.deleteDailyTaskById(taskUuid) }

        //then
        then(thrown)
            .isInstanceOf(RestInvalidRequest::class.java)
            .hasMessageContaining("Invalid UUID string:")

    }

    @Test
    fun `delete Daily Task but something happened`() {

        // given
        val taskUuid = UUID.randomUUID().toString()
        every { dailyTaskService.deleteDailyTask(any()) } throws RuntimeException("OHH NOOOO!")

        // when
        val thrown = catchThrowable { dailyTaskImpl.deleteDailyTaskById(taskUuid) }

        //then
        then(thrown)
            .isInstanceOf(RestException::class.java)
            .hasMessageContaining("OHH NOOOO!")

    }

    @Test
    fun `delete Daily Task`() {

        // given
        val taskUuid = UUID.randomUUID()
        every { dailyTaskService.deleteDailyTask(taskUuid) } returns mockk()

        // when
        val response = dailyTaskImpl.deleteDailyTaskById(taskUuid.toString())

        //then
        then(response.error).isNull()
        then(response.dailyTasks).isNotEmpty
        then(response.dailyTasks!!.size).isEqualTo(1)
        then(response.dailyTasks!!.first().identifier).isEqualTo(taskUuid.toString())

    }


    // endregion

    // region Parameters

    companion object {
        @JvmStatic
        fun dailyTaskCheckMandatoryParams(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    null,
                    UUID.randomUUID().toString(),
                    "2020-04-04",
                    RestInvalidRequest::class.java,
                    "Missing clientIdentifierCreator param"
                ),
                Arguments.of(
                    UUID.randomUUID().toString(),
                    null,
                    "2020-04-04",
                    RestInvalidRequest::class.java,
                    "Missing clientIdentifierTask param"
                ),
            )
        }

        @JvmStatic
        fun dailyTaskCheckInvalidParams(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    "INVALID",
                    UUID.randomUUID().toString(),
                    "2020-04-04",
                    RestInvalidRequest::class.java,
                    "Invalid UUID string:"
                ),
                Arguments.of(
                    UUID.randomUUID().toString(),
                    "INVALID",
                    "2020-04-04",
                    RestInvalidRequest::class.java,
                    "Invalid UUID string:"
                ),
                Arguments.of(
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    "2020-04",
                    RestInvalidRequest::class.java,
                    "Invalid format date. Date:"
                ),
            )
        }

    }

    // endregion
}