package com.coach.flame.api.dailyTask

import com.coach.flame.api.dailyTask.request.*
import com.coach.flame.dailyTask.DailyTaskService
import com.coach.flame.date.DateHelper
import com.coach.flame.domain.DailyTaskDto
import com.coach.flame.domain.maker.DailyTaskDtoBuilder
import com.coach.flame.exception.RestException
import com.coach.flame.exception.RestInvalidRequestException
import com.natpryce.makeiteasy.MakeItEasy.with
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.util.*
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
class DailyTaskImplTest {

    @MockK
    private lateinit var dailyTaskService: DailyTaskService

    @InjectMockKs
    private lateinit var dailyTaskImpl: DailyTaskImpl

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    // region Daily Task [Create]

    @ParameterizedTest
    @MethodSource("dailyTaskCheckMandatoryParams")
    fun `test create Daily Task with mandatory params`(
        taskName: String?,
        taskDescription: String?,
        date: String?,
        expectedException: Class<Any>,
        expectedMessage: String,
    ) {

        // given
        val task = DailyTaskRequestBuilder.maker()
            .but(with(DailyTaskRequestMaker.name, taskName),
                with(DailyTaskRequestMaker.description, taskDescription),
                with(DailyTaskRequestMaker.date, date))
            .make()

        // when
        val thrown = catchThrowable { dailyTaskImpl.createDailyTask(UUID.randomUUID(), UUID.randomUUID(), task) }

        //then
        then(thrown)
            .isInstanceOf(expectedException)
            .hasMessageContaining(expectedMessage)
    }

    @ParameterizedTest
    @MethodSource("dailyTaskCheckInvalidParams")
    fun `test create Daily Task with invalid format params`(
        date: String?,
        expectedException: Class<Any>,
        expectedMessage: String,
    ) {

        // given
        val task = DailyTaskRequestBuilder.maker()
            .but(with(DailyTaskRequestMaker.date, date))
            .make()

        // when
        val thrown = catchThrowable { dailyTaskImpl.createDailyTask(UUID.randomUUID(), UUID.randomUUID(), task) }

        //then
        then(thrown)
            .isInstanceOf(expectedException)
            .hasMessageContaining(expectedMessage)
    }

    @Test
    fun `test create Daily Task when business throw an exception`() {

        // given
        val task = DailyTaskRequestBuilder.default()
        every { dailyTaskService.createDailyTask(any()) } throws RuntimeException("OHH NOOOO!")

        // when
        val thrown = catchThrowable { dailyTaskImpl.createDailyTask(UUID.randomUUID(), UUID.randomUUID(), task) }

        //then
        then(thrown)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageContaining("OHH NOOOO!")
    }

    @Test
    fun `test create Daily Task`() {

        // given
        val taskDto = slot<DailyTaskDto>()
        val coachToken = UUID.randomUUID()
        val clientToken = UUID.randomUUID()
        val task = DailyTaskRequestBuilder.default()
        every { dailyTaskService.createDailyTask(capture(taskDto)) } answers { taskDto.captured }

        // when
        val response = dailyTaskImpl.createDailyTask(clientToken, coachToken, task)

        //then
        then(taskDto.captured.identifier).isNotNull
        then(taskDto.captured.name).isEqualTo(task.taskName)
        then(taskDto.captured.description).isEqualTo(task.taskDescription)
        then(taskDto.captured.date).isEqualTo(DateHelper.toDate(task.date!!))
        then(taskDto.captured.ticked).isFalse
        then(taskDto.captured.clientIdentifier).isEqualTo(clientToken)
        then(taskDto.captured.coachIdentifier).isEqualTo(coachToken)
        then(response.dailyTasks).isNotEmpty

        val dailyTask = response.dailyTasks.first()

        then(dailyTask.identifier).isEqualTo(taskDto.captured.identifier.toString())
        then(dailyTask.taskName).isEqualTo(taskDto.captured.name)
        then(dailyTask.taskDescription).isEqualTo(taskDto.captured.description)
        then(dailyTask.date).isEqualTo(taskDto.captured.date.toString())
        then(dailyTask.ticked).isFalse

    }

    @Test
    fun `test create multiple Daily Task`() {

        // given
        val taskDto = slot<DailyTaskDto>()
        val coachToken = UUID.randomUUID()
        val clientToken = UUID.randomUUID()
        val listOfTasks = setOf(DailyTaskDtoBuilder.default(),
            DailyTaskDtoBuilder.default(),
            DailyTaskDtoBuilder.default(),
            DailyTaskDtoBuilder.default(),
            DailyTaskDtoBuilder.default())
        val task = DailyTaskRequestBuilder.maker()
            .but(with(DailyTaskRequestMaker.date, "2021-06-10"),
                with(DailyTaskRequestMaker.toDate, "2021-06-25"))
            .make()
        every {
            dailyTaskService.createDailyTask(capture(taskDto),
                eq(LocalDate.parse("2021-06-25")))
        } returns listOfTasks

        // when
        val response = dailyTaskImpl.createDailyTask(clientToken, coachToken, task)

        then(response.dailyTasks).hasSize(5)

    }

    // endregion

    // region Daily Task [Get]

    @Test
    fun `get Daily Task by client but something happened`() {

        // given
        val clientUUID = UUID.randomUUID()
        every { dailyTaskService.getDailyTasksByClient(clientUUID) } throws RuntimeException("OHH NOOOO!")

        // when
        val thrown = catchThrowable { dailyTaskImpl.getDailyTasksByClient(clientUUID) }

        //then
        then(thrown)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageContaining("OHH NOOOO!")
    }

    @Test
    fun `get Daily Task by client`() {

        // given
        val clientUUID = UUID.randomUUID()
        val task1 = DailyTaskDtoBuilder.default()
        val task2 = DailyTaskDtoBuilder.default()
        every { dailyTaskService.getDailyTasksByClient(clientUUID) } returns setOf(task1, task2)

        // when
        val response = dailyTaskImpl.getDailyTasksByClient(clientUUID)

        //then
        then(response.dailyTasks).isNotEmpty
        then(response.dailyTasks.size).isEqualTo(2)

        val taskResponseT1 = response.dailyTasks.find { task1.identifier.toString() == it.identifier }
        then(taskResponseT1?.taskName).isEqualTo(task1.name)
        then(taskResponseT1?.taskDescription).isEqualTo(task1.description)
        then(taskResponseT1?.date).isEqualTo(task1.date.toString())
        then(taskResponseT1?.ticked).isEqualTo(task1.ticked)

    }

    @Test
    fun `get Daily Task using empty filters empty`() {

        // given
        val request = DailyTaskFiltersRequestBuilder.default()

        // when
        val thrown = catchThrowable { dailyTaskImpl.getDailyTasksUsingFilters(request) }

        //then
        then(thrown)
            .isInstanceOf(RestException::class.java)
            .hasMessageContaining("filters can not be empty")
    }

    @Test
    fun `get Daily Task using invalid filters`() {

        // given
        val filter0 = DailyTaskFilterBuilder.maker()
            .but(with(DailyTaskFilterMaker.type, "IDENTIFIER"),
                with(DailyTaskFilterMaker.values, listOf(UUID.randomUUID().toString())))
            .make()
        val filter1 = DailyTaskFilterBuilder.default()
        val request = DailyTaskFiltersRequestBuilder.maker()
            .but(with(DailyTaskFiltersRequestMaker.filters, setOf(filter0, filter1)))
            .make()

        // when
        val thrown = catchThrowable { dailyTaskImpl.getDailyTasksUsingFilters(request) }

        //then
        then(thrown)
            .isInstanceOf(RestException::class.java)
            .hasMessageContaining("INVALID is an invalid filter")
    }

    @Test
    fun `get Daily Task using multiple filters`() {

        // given
        val filter0 = DailyTaskFilterBuilder.maker()
            .but(with(DailyTaskFilterMaker.type, "IDENTIFIER"),
                with(DailyTaskFilterMaker.values, listOf(UUID.randomUUID().toString())))
            .make()
        val filter1 = DailyTaskFilterBuilder.maker()
            .but(with(DailyTaskFilterMaker.type, "BETWEEN_DATES"),
                with(DailyTaskFilterMaker.values, listOf(LocalDate.now().toString(), LocalDate.now().toString())))
            .make()
        val request = DailyTaskFiltersRequestBuilder.maker()
            .but(with(DailyTaskFiltersRequestMaker.filters, setOf(filter0, filter1)))
            .make()

        val dailyTask0 = DailyTaskDtoBuilder.default()
        val dailyTask1 = DailyTaskDtoBuilder.default()

        every { dailyTaskService.getDailyTasksUsingFilters(any()) } returns setOf(dailyTask0, dailyTask1)

        // when
        val response = dailyTaskImpl.getDailyTasksUsingFilters(request)

        //then
        then(response.dailyTasks).isNotEmpty
        then(response.dailyTasks.size).isEqualTo(2)

        val task0 = response.dailyTasks.find { it.identifier == dailyTask0.identifier.toString() }
        then(task0?.taskName).isEqualTo(dailyTask0.name)
        then(task0?.taskDescription).isEqualTo(dailyTask0.description)
        then(task0?.date).isEqualTo(dailyTask0.date.toString())
        then(task0?.ticked).isEqualTo(dailyTask0.ticked)

    }

    // endregion

    // region Daily Task [Delete]

    @Test
    fun `delete Daily Task check invalid params`() {

        // given
        val taskUuid = "INVALID"

        // when
        val thrown = catchThrowable { dailyTaskImpl.deleteDailyTask(taskUuid) }

        //then
        then(thrown)
            .isInstanceOf(RestInvalidRequestException::class.java)
            .hasMessageContaining("Invalid UUID string:")

    }

    @Test
    fun `delete Daily Task but something happened`() {

        // given
        val taskUuid = UUID.randomUUID().toString()
        every { dailyTaskService.deleteDailyTask(any()) } throws RuntimeException("OHH NOOOO!")

        // when
        val thrown = catchThrowable { dailyTaskImpl.deleteDailyTask(taskUuid) }

        //then
        then(thrown)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageContaining("OHH NOOOO!")

    }

    @Test
    fun `delete Daily Task`() {

        // given
        val taskUuid = UUID.randomUUID()
        every { dailyTaskService.deleteDailyTask(taskUuid) } returns mockk()

        // when
        val response = dailyTaskImpl.deleteDailyTask(taskUuid.toString())

        //then
        then(response.dailyTasks).isNotEmpty
        then(response.dailyTasks.size).isEqualTo(1)
        then(response.dailyTasks.first().identifier).isEqualTo(taskUuid.toString())

    }


    // endregion

    // region Parameters

    companion object {
        @JvmStatic
        fun dailyTaskCheckMandatoryParams(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    null,
                    "Drink 1L water",
                    "2020-04-04",
                    RestInvalidRequestException::class.java,
                    "Missing taskName param"
                ),
                Arguments.of(
                    "Drink Water",
                    null,
                    "2020-04-04",
                    RestInvalidRequestException::class.java,
                    "Missing taskDescription param"
                ),
                Arguments.of(
                    "Drink Water",
                    "Drink 1L of Water",
                    null,
                    RestInvalidRequestException::class.java,
                    "Missing date param"
                ),
            )
        }

        @JvmStatic
        fun dailyTaskCheckInvalidParams(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    "2020-04",
                    RestInvalidRequestException::class.java,
                    "Invalid date format. Date:"
                ),
            )
        }

    }

    // endregion
}
