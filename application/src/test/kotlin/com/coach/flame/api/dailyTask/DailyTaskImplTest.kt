package com.coach.flame.api.dailyTask

import com.coach.flame.api.dailyTask.request.*
import com.coach.flame.dailyTask.DailyTaskService
import com.coach.flame.date.stringToDate
import com.coach.flame.domain.DailyTaskDto
import com.coach.flame.domain.maker.DailyTaskDtoBuilder
import com.coach.flame.domain.maker.DailyTaskDtoMaker
import com.coach.flame.exception.RestException
import com.coach.flame.exception.RestInvalidRequestException
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.MakeItEasy.with
import com.natpryce.makeiteasy.Maker
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
import org.junit.jupiter.api.BeforeEach
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

    private lateinit var dailyTaskDtoMaker: Maker<DailyTaskDto>
    private lateinit var dailyTaskRequestMaker: Maker<DailyTaskRequest>

    @BeforeEach
    fun setUp() {
        dailyTaskDtoMaker = an(DailyTaskDtoMaker.DailyTaskDto)
        dailyTaskRequestMaker = an(DailyTaskRequestMaker.DailyTaskRequest)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    // region Daily Task [Create]

    @Test
    fun `test create multiple Daily Task`() {

        // given
        val listOfTasks = listOf(dailyTaskRequestMaker.make(), dailyTaskRequestMaker.make())

        // when
        val thrown = catchThrowable { dailyTaskImpl.createDailyTasks(listOfTasks) }

        //then
        then(thrown)
            .isInstanceOf(RestException::class.java)
            .hasMessage("This is not supported yet")
    }

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
        val task = dailyTaskRequestMaker
            .but(with(DailyTaskRequestMaker.name, taskName))
            .but(with(DailyTaskRequestMaker.description, taskDescription))
            .but(with(DailyTaskRequestMaker.date, date))
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
        val task = dailyTaskRequestMaker
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
        val task = dailyTaskRequestMaker.make()
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
        val task = dailyTaskRequestMaker.make()
        every { dailyTaskService.createDailyTask(capture(taskDto)) } answers { taskDto.captured }

        // when
        val response = dailyTaskImpl.createDailyTask(clientToken, coachToken, task)

        //then
        then(taskDto.captured.identifier).isNotNull
        then(taskDto.captured.name).isEqualTo(task.taskName)
        then(taskDto.captured.description).isEqualTo(task.taskDescription)
        then(taskDto.captured.date).isEqualTo(stringToDate(task.date!!))
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
        val task1 = dailyTaskDtoMaker.make()
        val task2 = dailyTaskDtoMaker.make()
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
                    "Invalid format date. Date:"
                ),
            )
        }

    }

    // endregion
}
