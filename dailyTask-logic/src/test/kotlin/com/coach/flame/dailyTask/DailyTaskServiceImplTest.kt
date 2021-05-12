package com.coach.flame.dailyTask

import com.coach.flame.dailyTask.filter.BetweenDatesFilter
import com.coach.flame.dailyTask.filter.IdentifierFilter
import com.coach.flame.domain.maker.DailyTaskDtoBuilder
import com.coach.flame.domain.maker.DailyTaskDtoMaker
import com.coach.flame.jpa.entity.DailyTask
import com.coach.flame.jpa.entity.maker.ClientBuilder
import com.coach.flame.jpa.entity.maker.CoachBuilder
import com.coach.flame.jpa.entity.maker.DailyTaskBuilder
import com.coach.flame.jpa.entity.maker.DailyTaskMaker
import com.coach.flame.jpa.repository.ClientRepository
import com.coach.flame.jpa.repository.CoachRepository
import com.coach.flame.jpa.repository.DailyTaskRepository
import com.natpryce.makeiteasy.MakeItEasy.with
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.BDDAssertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.jpa.domain.Specification
import java.sql.SQLException
import java.time.LocalDate
import java.util.*

@ExtendWith(MockKExtension::class)
class DailyTaskServiceImplTest {

    @MockK
    private lateinit var dailyTaskRepository: DailyTaskRepository

    @MockK
    private lateinit var clientRepository: ClientRepository

    @MockK
    private lateinit var coachRepository: CoachRepository

    @InjectMockKs
    private lateinit var dailyTaskServiceImpl: DailyTaskServiceImpl

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `delete valid daily task`() {

        // given
        val uuid = UUID.randomUUID()
        every { dailyTaskRepository.deleteByUuid(uuid = uuid) } returns 1

        // when & then
        thenNoException().isThrownBy { dailyTaskServiceImpl.deleteDailyTask(uuid) }
    }

    @Test
    fun `delete invalid daily task`() {

        // given
        val uuid = UUID.randomUUID()
        every { dailyTaskRepository.deleteByUuid(uuid = uuid) } returns 0

        // when & then
        thenExceptionOfType(DailyTaskMissingDeleteException::class.java)
            .isThrownBy { dailyTaskServiceImpl.deleteDailyTask(uuid) }

    }

    @Test
    fun `get daily task by id`() {

        // given
        val taskId = 100L
        val dailyTask: DailyTask = DailyTaskBuilder.default()

        every { dailyTaskRepository.findById(taskId) } returns Optional.of(dailyTask)

        //when
        val dailyTaskDto = dailyTaskServiceImpl.getDailyTaskById(taskId)

        //then
        then(dailyTaskDto.name).isEqualTo(dailyTask.name)
        then(dailyTaskDto.identifier).isEqualTo(dailyTask.uuid)
        then(dailyTaskDto.description).isEqualTo(dailyTask.description)
        then(dailyTaskDto.date).isEqualTo(dailyTask.date)
        then(dailyTaskDto.ticked).isEqualTo(dailyTask.ticked)

    }

    @Test
    fun `get invalid daily task by id`() {

        // given
        val taskId = 100L

        every { dailyTaskRepository.findById(taskId) } returns Optional.empty()

        //when and then
        thenExceptionOfType(DailyTaskNotFoundException::class.java)
            .isThrownBy { dailyTaskServiceImpl.getDailyTaskById(taskId) }

    }

    @Test
    fun `get daily tasks by client`() {

        // given
        val clientUUID = UUID.randomUUID()
        val task1 = DailyTaskBuilder.default()
        val task2 = DailyTaskBuilder.default()
        val listOfTasks = setOf(task1, task2)
        every { dailyTaskRepository.findAllByClient(clientUUID) } returns Optional.of(listOfTasks)

        // when
        val listOfDailyTasks = dailyTaskServiceImpl.getDailyTasksByClient(clientUUID)

        // then
        then(listOfDailyTasks).hasSize(2)

    }

    @Test
    fun `get daily tasks using filters`() {

        // given
        val dateNow = LocalDate.now()
        val uuid = UUID.randomUUID()
        val filter1 = IdentifierFilter(uuid)
        val filter2 = BetweenDatesFilter(dateNow.minusDays(1), dateNow.plusDays(1))
        val criteria = slot<Specification<DailyTask>>()
        val listOfDailyTask = listOf(DailyTaskBuilder.default(), DailyTaskBuilder.default())

        every { dailyTaskRepository.findAll(capture(criteria)) } answers { listOfDailyTask }

        // when
        val listOfDailyTasks = dailyTaskServiceImpl.getDailyTasksUsingFilters(setOf(filter1, filter2))

        // then
        then(listOfDailyTasks).hasSize(2)

    }

    @Test
    fun `get empty daily tasks by client`() {

        // given
        val clientUUID = UUID.randomUUID()
        every { dailyTaskRepository.findAllByClient(clientUUID) } returns Optional.empty()

        // when
        val listOfDailyTasks = dailyTaskServiceImpl.getDailyTasksByClient(clientUUID)

        // then
        then(listOfDailyTasks).hasSize(0)

    }

    @Test
    fun `create daily task when not found the coach identifier`() {

        // given
        every { coachRepository.findByUuid(any()) } returns null
        every { clientRepository.findByUuid(any()) } returns mockk()

        // when & then
        thenExceptionOfType(CustomerNotFoundException::class.java)
            .isThrownBy { dailyTaskServiceImpl.createDailyTask(DailyTaskDtoBuilder.default()) }
            .withMessage("Didn't find any coach with this identifier, please check the coach identifier.")

    }

    @Test
    fun `create daily task when not found the client identifier`() {

        // given
        every { coachRepository.findByUuid(any()) } returns mockk()
        every { clientRepository.findByUuid(any()) } returns null

        // when & then
        thenExceptionOfType(CustomerNotFoundException::class.java)
            .isThrownBy { dailyTaskServiceImpl.createDailyTask(DailyTaskDtoBuilder.default()) }
            .withMessage("Didn't find any client with this identifier, please check the client identifier.")

    }

    @Test
    fun `create daily task when something wrong happened`() {

        // given
        every { clientRepository.findByUuid(any()) } returns ClientBuilder.default()
        every { coachRepository.findByUuid(any()) } returns CoachBuilder.default()
        every { dailyTaskRepository.save(any()) } throws SQLException("SQL error message")

        // when & then
        thenExceptionOfType(Exception::class.java)
            .isThrownBy { dailyTaskServiceImpl.createDailyTask(DailyTaskDtoBuilder.default()) }
            .withMessage("SQL error message")

    }

    @Test
    fun `update daily task when not found the daily task`() {

        // given
        val dailyTaskDto = DailyTaskDtoBuilder.default()
        every { dailyTaskRepository.findByUuid(dailyTaskDto.identifier) } returns null

        // when & then
        thenExceptionOfType(DailyTaskNotFoundException::class.java)
            .isThrownBy { dailyTaskServiceImpl.updateDailyTask(dailyTaskDto) }
            .withMessage("Daily task not found, please check the identifier.")

    }

    @Test
    fun `update daily task`() {

        val entity = slot<DailyTask>()

        // given
        val dailyTaskDto = DailyTaskDtoBuilder.maker()
            .but(with(DailyTaskDtoMaker.name, "Test Update"),
                with(DailyTaskDtoMaker.description, "Valid description"),
                with(DailyTaskDtoMaker.date, LocalDate.now()),
                with(DailyTaskDtoMaker.ticked, true))
            .make()

        val dailyTaskEntity = DailyTaskBuilder.maker()
            .but(with(DailyTaskMaker.client, ClientBuilder.default()),
                with(DailyTaskMaker.createdBy, CoachBuilder.default()))
            .make()
        every { dailyTaskRepository.findByUuid(dailyTaskDto.identifier) } returns dailyTaskEntity
        every { dailyTaskRepository.save(capture(entity)) } answers { entity.captured }

        // when
        val dailyTask = dailyTaskServiceImpl.updateDailyTask(dailyTaskDto)

        // then
        then(entity.isCaptured).isTrue
        then(dailyTask.identifier).isEqualTo(entity.captured.uuid)
        then(dailyTask.name).isEqualTo(dailyTaskDto.name)
        then(dailyTask.description).isEqualTo(dailyTaskDto.description)
        then(dailyTask.date).isEqualTo(dailyTaskDto.date)
        then(dailyTask.ticked).isEqualTo(dailyTaskDto.ticked)
        then(dailyTask.coachIdentifier).isEqualTo(entity.captured.createdBy.uuid)
        then(dailyTask.clientIdentifier).isEqualTo(entity.captured.client.uuid)

    }

    @Test
    fun `create daily task`() {

        val entity = slot<DailyTask>()

        // given
        val dailyTaskDto = DailyTaskDtoBuilder.default()
        val coach = CoachBuilder.default()
        val client = ClientBuilder.default()
        val postDailyTask = DailyTaskBuilder.maker()
            .but(with(DailyTaskMaker.client, client))
            .make()
        every { clientRepository.findByUuid(dailyTaskDto.clientIdentifier!!) } returns client
        every { coachRepository.findByUuid(dailyTaskDto.coachIdentifier!!) } returns coach
        every { dailyTaskRepository.save(capture(entity)) } returns postDailyTask

        // when
        val dailyTask = dailyTaskServiceImpl.createDailyTask(dailyTaskDto)

        // then
        then(entity.isCaptured).isTrue
        then(entity.captured.uuid).isEqualTo(dailyTaskDto.identifier)
        then(entity.captured.name).isEqualTo(dailyTaskDto.name)
        then(entity.captured.description).isEqualTo(dailyTaskDto.description)
        then(entity.captured.date).isEqualTo(dailyTaskDto.date)
        then(entity.captured.ticked).isFalse
        then(entity.captured.createdBy).isEqualTo(coach)
        then(entity.captured.client).isEqualTo(client)
        then(dailyTask.coachIdentifier).isEqualTo(postDailyTask.createdBy.uuid)
        then(dailyTask.clientIdentifier).isEqualTo(postDailyTask.client.uuid)

    }

}
