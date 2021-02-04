package com.coach.flame.dailyTask

import com.coach.flame.dailyTask.domain.DailyTaskDto
import com.coach.flame.jpa.entity.DailyTask
import com.coach.flame.jpa.repository.ClientRepository
import com.coach.flame.jpa.repository.DailyTaskRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.Date
import java.time.LocalDate
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

        val dailyTask = dailyTaskRepository.findById(taskId)

        if (dailyTask.isEmpty) {
            throw DailyTaskNotFound("Could not found any daily task with id: $taskId")
        }

        return DailyTaskDto(
            identifier = dailyTask.get().uuid,
            name = dailyTask.get().name,
            description = dailyTask.get().description,
            date = dailyTask.get().date.toLocalDate(),
            ticked = dailyTask.get().ticked
        )

    }

    override fun getDailyTasksByClient(clientId: Long): Set<DailyTaskDto> {

        val dailyTasks = dailyTaskRepository.findAllByClient(clientId)

        if (dailyTasks.isEmpty) {
            return setOf()
        }

        return dailyTasks
            .get()
            .map { entity ->
                DailyTaskDto(
                    identifier = entity.uuid,
                    name = entity.name,
                    description = entity.description,
                    date = entity.date.toLocalDate(),
                    ticked = entity.ticked
                )
            }
            .toSet()

    }

    override fun createDailyTask(dailyTask: DailyTaskDto) {

        val createdBy = clientRepository.findByUuid(dailyTask.identifier)

        checkNotNull(createdBy) { "Could not find any client with the following identifier ${dailyTask.identifier}" }

        val owner = clientRepository.findByUuid(dailyTask.identifier)

        checkNotNull(owner) { "Could not find any client with the following identifier ${dailyTask.identifier}" }

        val newDailyTask = DailyTask(
            uuid = dailyTask.identifier,
            name = dailyTask.name,
            description = dailyTask.description,
            date = Date.valueOf(LocalDate.now()),
            ticked = false,
            createdBy = createdBy,
            client = owner
        )

        try {
            dailyTaskRepository.save(newDailyTask)
        } catch (ex: IllegalArgumentException) {
            throw DailyTaskMissingSave("Daily task couldn't be persisted.", ex)
        }

    }

    override fun deleteDailyTask(uuid: UUID): Pair<String, Boolean> {
        TODO("Not yet implemented")
    }
}