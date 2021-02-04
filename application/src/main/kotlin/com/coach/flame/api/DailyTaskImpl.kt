package com.coach.flame.api

import com.coach.flame.api.request.DailyTaskRequest
import com.coach.flame.api.response.DailyTask
import com.coach.flame.api.response.DailyTaskResponse
import com.coach.flame.aspect.LoggingRequest
import com.coach.flame.aspect.LoggingResponse
import com.coach.flame.dailyTask.DailyTaskService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.*

@RestController
@RequestMapping("/api/dailyTask")
class DailyTaskImpl(
    @Autowired private val dailyTaskService: DailyTaskService
) : DailyTaskAPI {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(DailyTaskImpl::class.java)
    }

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/create")
    @ResponseBody
    override fun createDailyTasks(
        @RequestBody(required = true) request: List<DailyTaskRequest>
    ): DailyTaskResponse {

        return DailyTaskResponse(
        )

    }

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/get/client/{clientId}")
    @ResponseBody
    override fun getDailyTasksByClient(
        @PathVariable(required = true) clientId: Long
    ): DailyTaskResponse {

        val dailyTasksDto = dailyTaskService.getDailyTasksByClient(clientId)

        return DailyTaskResponse(
            dailyTasks = dailyTasksDto
                .map { dto ->
                    DailyTask(
                        identifier = dto.identifier.toString(),
                        name = dto.name,
                        description = dto.description,
                        date = dto.date.toString(),
                        ticked = dto.ticked
                    )
                }
                .toSet()
        )
    }

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/get/{taskId}")
    override fun getDailyTaskById(
        @PathVariable(required = true) taskId: Long
    ): DailyTaskResponse {

        val dailyTask = dailyTaskService.getDailyTaskById(taskId)

        return DailyTaskResponse(
            dailyTasks = setOf(
                DailyTask(
                    identifier = dailyTask.identifier.toString(),
                    name = dailyTask.name,
                    description = dailyTask.description,
                    date = dailyTask.date.toString(),
                    ticked = dailyTask.ticked
                )
            )
        )
    }

    @LoggingRequest
    @LoggingResponse
    @DeleteMapping("/delete/{taskId}")
    override fun deleteDailyTaskById(
        @PathVariable(required = true) taskId: Long
    ): DailyTaskResponse {

        return DailyTaskResponse(

        )

    }

}