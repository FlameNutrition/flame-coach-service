package com.coach.flame.dailyTask

import com.coach.flame.dailyTask.domain.DailyTaskDto
import com.coach.flame.jpa.entity.DailyTask
import com.coach.flame.jpa.repository.ClientRepository
import com.coach.flame.jpa.repository.DailyTaskRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Date
import java.util.*

@Service
class DailyTaskServiceImpl(
    @Autowired private val dailyTaskRepository: DailyTaskRepository,
    @Autowired private val clientRepository: ClientRepository
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
    override fun createDailyTask(dailyTask: DailyTaskDto): UUID {

        try {

            LOGGER.info("opr='createDailyTask', msg='Creating the following task', dailyTask=$dailyTask")

            val createdBy = clientRepository.findByUuid(dailyTask.createdBy!!.identifier)

            checkNotNull(createdBy) { "Could not find any client with the following identifier ${dailyTask.createdBy.identifier}" }

            val owner = clientRepository.findByUuid(dailyTask.owner!!.identifier)

            checkNotNull(owner) { "Could not find any client with the following identifier ${dailyTask.owner.identifier}" }

            val newDailyTask = DailyTask(
                uuid = dailyTask.identifier,
                name = dailyTask.name,
                description = dailyTask.description,
                date = Date.valueOf(dailyTask.date),
                ticked = false,
                createdBy = createdBy,
                client = owner
            )

            val entity = dailyTaskRepository.save(newDailyTask)

            return entity.uuid
        } catch (ex: IllegalStateException) {
            throw ClientNotFound(ex)
        } catch (ex: Exception) {
            throw DailyTaskMissingSave("Daily task couldn't be persisted.", ex)
        }

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
            date = entity.date.toLocalDate(),
            ticked = entity.ticked
        )
    }

}