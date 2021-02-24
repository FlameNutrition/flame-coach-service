package com.coach.flame.dailyTask

import com.coach.flame.domain.DailyTaskDtoGenerator
import com.coach.flame.jpa.entity.ClientGenerator
import com.coach.flame.jpa.entity.DailyTask
import com.coach.flame.jpa.entity.DailyTaskGenerator
import com.coach.flame.jpa.repository.ClientRepository
import com.coach.flame.jpa.repository.DailyTaskRepository
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import org.assertj.core.api.BDDAssertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.sql.Date
import java.util.*

@ExtendWith(MockKExtension::class)
class DailyTaskServiceImplTest {

    @MockK
    private lateinit var dailyTaskRepository: DailyTaskRepository

    @MockK
    private lateinit var clientRepository: ClientRepository

    @InjectMockKs
    private lateinit var dailyTaskServiceImpl: DailyTaskServiceImpl

    @AfterEach
    fun setUp() {
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
        val dailyTask: DailyTask = DailyTaskGenerator.Builder().build().nextObject()

        every { dailyTaskRepository.findById(taskId) } returns Optional.of(dailyTask)

        //when
        val dailyTaskDto = dailyTaskServiceImpl.getDailyTaskById(taskId)

        //then
        then(dailyTaskDto.name).isEqualTo(dailyTask.name)
        then(dailyTaskDto.identifier).isEqualTo(dailyTask.uuid)
        then(dailyTaskDto.description).isEqualTo(dailyTask.description)
        then(dailyTaskDto.date).isEqualTo(dailyTask.date.toLocalDate())
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
        val builder = DailyTaskGenerator.Builder().build()
        val task1 = builder.nextObject()
        val task2 = builder.nextObject()
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
    fun `create daily task when not found the created client`() {

        // given
        val dailyTask = DailyTaskDtoGenerator.Builder().build().nextObject()
        every { clientRepository.findByUuid(dailyTask.createdBy!!.identifier) } returns null

        // when & then
        thenExceptionOfType(ClientNotFound::class.java)
            .isThrownBy { dailyTaskServiceImpl.createDailyTask(dailyTask) }
            .withMessage(
                "java.lang.IllegalStateException: Could not find any client " +
                        "with the following identifier ${dailyTask.createdBy!!.identifier}"
            )

    }

    @Test
    fun `create daily task when not found the owner client`() {

        // given
        val dailyTask = DailyTaskDtoGenerator.Builder().build().nextObject()
        val createdByClient = ClientGenerator.Builder().build().nextObject()
        every { clientRepository.findByUuid(dailyTask.createdBy!!.identifier) } returns createdByClient
        every { clientRepository.findByUuid(dailyTask.owner!!.identifier) } returns null

        // when & then
        thenExceptionOfType(ClientNotFound::class.java)
            .isThrownBy { dailyTaskServiceImpl.createDailyTask(dailyTask) }
            .withMessage(
                "java.lang.IllegalStateException: Could not find any client " +
                        "with the following identifier ${dailyTask.owner!!.identifier}"
            )

    }

    @Test
    fun `create daily task when something wrong happened`() {

        // given
        val dailyTask = DailyTaskDtoGenerator.Builder().build().nextObject()
        val createdByClient = ClientGenerator.Builder().build().nextObject()
        val ownerClient = ClientGenerator.Builder().build().nextObject()
        every { clientRepository.findByUuid(dailyTask.createdBy!!.identifier) } returns createdByClient
        every { clientRepository.findByUuid(dailyTask.owner!!.identifier) } returns ownerClient
        every { dailyTaskRepository.save(any()) } throws Exception("SQL error message")

        // when & then
        thenExceptionOfType(Exception::class.java)
            .isThrownBy { dailyTaskServiceImpl.createDailyTask(dailyTask) }
            .withMessage("Daily task couldn't be persisted.")

    }

    @Test
    fun `create daily task`() {

        val entity = slot<DailyTask>()

        val clientGenerator = ClientGenerator.Builder().build()

        // given
        val dailyTaskDto = DailyTaskDtoGenerator.Builder().build().nextObject()
        val createdByClient = clientGenerator.nextObject()
        val ownerClient = clientGenerator.nextObject()
        val dailyTask = DailyTaskGenerator.Builder().build().nextObject()
        every { clientRepository.findByUuid(dailyTaskDto.createdBy!!.identifier) } returns createdByClient
        every { clientRepository.findByUuid(dailyTaskDto.owner!!.identifier) } returns ownerClient
        every { dailyTaskRepository.save(capture(entity)) } returns dailyTask

        // when
        val entityUuid = dailyTaskServiceImpl.createDailyTask(dailyTaskDto)

        // then
        then(entity.isCaptured).isTrue
        then(entityUuid).isEqualTo(dailyTask.uuid)
        then(entity.captured.uuid).isEqualTo(dailyTaskDto.identifier)
        then(entity.captured.name).isEqualTo(dailyTaskDto.name)
        then(entity.captured.description).isEqualTo(dailyTaskDto.description)
        then(entity.captured.date).isEqualTo(Date.valueOf(dailyTaskDto.date))
        then(entity.captured.ticked).isFalse
        then(entity.captured.createdBy).isEqualTo(createdByClient)
        then(entity.captured.client).isEqualTo(ownerClient)

    }

}