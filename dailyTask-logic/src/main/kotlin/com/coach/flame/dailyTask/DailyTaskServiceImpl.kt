package com.coach.flame.dailyTask

import com.coach.flame.domain.DailyTaskDto
import com.coach.flame.jpa.entity.DailyTask
import com.coach.flame.jpa.entity.UserSession
import com.coach.flame.jpa.repository.ClientRepository
import com.coach.flame.jpa.repository.DailyTaskRepository
import com.coach.flame.jpa.repository.UserSessionRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.jvm.Throws

@Service
class DailyTaskServiceImpl(
    private val dailyTaskRepository: DailyTaskRepository,
    private val clientRepository: ClientRepository,
    private val userSessionRepository: UserSessionRepository,
) : DailyTaskService {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(DailyTaskServiceImpl::class.java)
    }

    override fun getDailyTaskById(taskId: Long): DailyTaskDto {

        LOGGER.info("opr='getDailyTaskById', msg='Get task by ID', taskId=$taskId")

        val dailyTask = dailyTaskRepository.findById(taskId)

        if (dailyTask.isEmpty) {
            throw DailyTaskNotFound("Could not found any daily task with id: $taskId")
        }

        return entityToDailyTaskDto(dailyTask.get())

    }

    override fun getDailyTasksByClient(clientId: Long): Set<DailyTaskDto> {

        LOGGER.info("opr='getDailyTasksByClient', msg='Get all tasks for a client', clientId=$clientId")

        val dailyTasks = dailyTaskRepository.findAllByClient(clientId)

        if (dailyTasks.isEmpty) {
            return setOf()
        }

        return dailyTasks
            .get()
            .map { entityToDailyTaskDto(it) }
            .toSet()

    }

    @Transactional
    override fun createDailyTask(dailyTask: DailyTaskDto): DailyTaskDto {

        LOGGER.info("opr='createDailyTask', msg='Creating the following task', dailyTask=$dailyTask")

        checkNotNull(dailyTask.coachToken) { "coachToken is a mandatory parameter" }
        checkNotNull(dailyTask.clientIdentifier) { "clientIdentifier is a mandatory parameter" }

        val coachSession = userSessionRepository.findByToken(dailyTask.coachToken!!)
            ?: run {
                LOGGER.warn("opr='createDailyTask', msg='Invalid coach token', coachToken={}", dailyTask.coachToken)
                throw ClientNotFoundException("Didn't find any coach session, please check the coachToken identifier.")
            }

        val client = clientRepository.findByUuid(dailyTask.clientIdentifier!!)
            ?: run {
                LOGGER.warn("opr='createDailyTask', msg='Invalid client identifier', clientIdentifier={}",
                    dailyTask.clientIdentifier)
                throw ClientNotFoundException("Didn't find any client with this identifier, please check the client identifier.")
            }

        val newDailyTask = DailyTask(
            uuid = dailyTask.identifier,
            name = dailyTask.name,
            description = dailyTask.description,
            date = dailyTask.date,
            ticked = false,
            createdBy = coachSession.user?.client!!,
            client = client
        )

        val entity = dailyTaskRepository.saveAndFlush(newDailyTask)

        return dailyTask.copy(
            coachToken = entity.createdBy.user.userSession.token,
            clientIdentifier = entity.client.uuid)

    }

    @Transactional
    override fun deleteDailyTask(uuid: UUID) {

        LOGGER.info("opr='deleteDailyTask', msg='Deleting the following task', uuid=$uuid")

        if (dailyTaskRepository.deleteByUuid(uuid) == 0) {
            throw DailyTaskMissingDelete("Didn't find the following uuid task: $uuid")
        }
    }

    private val entityToDailyTaskDto = { entity: DailyTask ->
        DailyTaskDto(
            identifier = entity.uuid,
            name = entity.name,
            description = entity.description,
            date = entity.date,
            ticked = entity.ticked
        )
    }

}