package com.coach.flame.dailyTask

import com.coach.flame.dailyTask.domain.DailyTaskDto
import com.coach.flame.jpa.repository.DailyTaskRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
class DailyTaskServiceImpl(
    @Autowired private val dailyTaskRepository: DailyTaskRepository
) : DailyTaskService {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(DailyTaskServiceImpl::class.java)
    }

    override fun getDailyTaskById(taskId: Long): DailyTaskDto {

        val dailyTask = dailyTaskRepository.findById(taskId)

        if (dailyTask.isEmpty) {
            throw BusinessElementNotFound("Could not found any daily task with id: $taskId")
        }

        return DailyTaskDto(
            identifier = dailyTask.get().uuid,
            name = dailyTask.get().name,
            description = dailyTask.get().description,
            date = dailyTask.get().date.toLocalDate()
        )

    }

    override fun getDailyTasksByClient(clientId: Long): Set<DailyTaskDto> {
        TODO("Not yet implemented")
    }

    override fun createDailyTask(dailyTask: DailyTaskDto): Pair<String, Boolean> {
        TODO("Not yet implemented")
    }

    override fun deleteDailyTask(dailyTask: DailyTaskDto): Pair<String, Boolean> {
        TODO("Not yet implemented")
    }
}