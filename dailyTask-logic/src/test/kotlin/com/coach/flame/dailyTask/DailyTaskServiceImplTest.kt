package com.coach.flame.dailyTask

import com.coach.flame.domain.DailyTaskDto
import com.coach.flame.domain.DailyTaskDtoMaker
import com.coach.flame.jpa.entity.*
import com.coach.flame.jpa.repository.ClientRepository
import com.coach.flame.jpa.repository.DailyTaskRepository
import com.coach.flame.jpa.repository.UserSessionRepository
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
import org.assertj.core.api.BDDAssertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.sql.SQLException
import java.util.*

@ExtendWith(MockKExtension::class)
class DailyTaskServiceImplTest {

    @MockK
    private lateinit var dailyTaskRepository: DailyTaskRepository

    @MockK
    private lateinit var userSessionRepository: UserSessionRepository

    @MockK
    private lateinit var clientRepository: ClientRepository

    @InjectMockKs
    private lateinit var dailyTaskServiceImpl: DailyTaskServiceImpl

    private lateinit var clientMaker: Maker<Client>
    private lateinit var dailyTaskMaker: Maker<DailyTask>
    private lateinit var dailyTaskDtoMaker: Maker<DailyTaskDto>
    private lateinit var userSessionMaker: Maker<UserSession>

    @BeforeEach
    fun setUp() {
        clientMaker = an(ClientMaker.Client)
        dailyTaskMaker = an(DailyTaskMaker.DailyTask)
        dailyTaskDtoMaker = an(DailyTaskDtoMaker.DailyTaskDto)
        userSessionMaker = an(UserSessionMaker.UserSession)
    }

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
        thenExceptionOfType(DailyTaskMissingDelete::class.java)
            .isThrownBy { dailyTaskServiceImpl.deleteDailyTask(uuid) }

    }

    @Test
    fun `get daily task by id`() {

        // given
        val taskId = 100L
        val dailyTask: DailyTask = dailyTaskMaker.make()

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
        thenExceptionOfType(DailyTaskNotFound::class.java)
            .isThrownBy { dailyTaskServiceImpl.getDailyTaskById(taskId) }

    }

    @Test
    fun `get daily tasks by client`() {

        // given
        val clientId = 20L
        val task1 = dailyTaskMaker.make()
        val task2 = dailyTaskMaker.make()
        val listOfTasks = setOf(task1, task2)
        every { dailyTaskRepository.findAllByClient(clientId) } returns Optional.of(listOfTasks)

        // when
        val listOfDailyTasks = dailyTaskServiceImpl.getDailyTasksByClient(clientId)

        // then
        then(listOfDailyTasks).hasSize(2)

    }

    @Test
    fun `get empty daily tasks by client`() {

        // given
        val clientId = 20L
        every { dailyTaskRepository.findAllByClient(clientId) } returns Optional.empty()

        // when
        val listOfDailyTasks = dailyTaskServiceImpl.getDailyTasksByClient(clientId)

        // then
        then(listOfDailyTasks).hasSize(0)

    }

    @Test
    fun `create daily task when not found the coach session`() {

        // given
        every { userSessionRepository.findByToken(any()) } returns null
        every { clientRepository.findByUuid(any()) } returns mockk()

        // when & then
        thenExceptionOfType(ClientNotFoundException::class.java)
            .isThrownBy { dailyTaskServiceImpl.createDailyTask(dailyTaskDtoMaker.make()) }
            .withMessage("Didn't find any coach session, please check the coachToken identifier.")

    }

    @Test
    fun `create daily task when not found the client identifier`() {

        // given
        every { userSessionRepository.findByToken(any()) } returns mockk()
        every { clientRepository.findByUuid(any()) } returns null

        // when & then
        thenExceptionOfType(ClientNotFoundException::class.java)
            .isThrownBy { dailyTaskServiceImpl.createDailyTask(dailyTaskDtoMaker.make()) }
            .withMessage("Didn't find any client with this identifier, please check the client identifier.")

    }

    @Test
    fun `create daily task when something wrong happened`() {

        // given
        val dailyTaskDto = dailyTaskDtoMaker.make()
        val coach = clientMaker.make()
        val coachSession = userSessionMaker
            .but(with(UserSessionMaker.token, dailyTaskDto.coachToken))
            .but(with(UserSessionMaker.client, coach))
            .make()
        every { userSessionRepository.findByToken(any()) } returns coachSession
        every { clientRepository.findByUuid(any()) } returns clientMaker.make()
        every { dailyTaskRepository.saveAndFlush(any()) } throws SQLException("SQL error message")

        // when & then
        thenExceptionOfType(Exception::class.java)
            .isThrownBy { dailyTaskServiceImpl.createDailyTask(dailyTaskDtoMaker.make()) }
            .withMessage("SQL error message")

    }

    @Test
    fun `create daily task`() {

        val entity = slot<DailyTask>()

        // given
        val dailyTaskDto = dailyTaskDtoMaker.make()
        val coach = clientMaker.make()
        val coachSession = userSessionMaker
            .but(with(UserSessionMaker.token, dailyTaskDto.coachToken))
            .but(with(UserSessionMaker.client, coach))
            .make()
        val client = clientMaker.make()
        val postDailyTask = dailyTaskMaker
            .but(with(DailyTaskMaker.createdBy, coach))
            .but(with(DailyTaskMaker.client, client))
            .make()
        every { userSessionRepository.findByToken(dailyTaskDto.coachToken!!) } returns coachSession
        every { clientRepository.findByUuid(dailyTaskDto.clientIdentifier!!) } returns client
        every { dailyTaskRepository.saveAndFlush(capture(entity)) } returns postDailyTask

        // when
        val dailyTask = dailyTaskServiceImpl.createDailyTask(dailyTaskDto)

        // then
        then(entity.isCaptured).isTrue
        then(entity.captured.uuid).isEqualTo(dailyTaskDto.identifier)
        then(entity.captured.name).isEqualTo(dailyTaskDto.name)
        then(entity.captured.description).isEqualTo(dailyTaskDto.description)
        then(entity.captured.date).isEqualTo(dailyTaskDto.date)
        then(entity.captured.ticked).isFalse
        then(entity.captured.createdBy).isEqualTo(coachSession.client)
        then(entity.captured.client).isEqualTo(client)
        then(dailyTask.coachToken).isEqualTo(postDailyTask.createdBy.userSession?.token)
        then(dailyTask.clientIdentifier).isEqualTo(postDailyTask.client.uuid)

    }

}