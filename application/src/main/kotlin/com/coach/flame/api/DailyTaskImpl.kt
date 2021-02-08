package com.coach.flame.api

import com.coach.flame.api.request.DailyTaskRequest
import com.coach.flame.api.response.DailyTask
import com.coach.flame.api.response.DailyTaskResponse
import com.coach.flame.aspect.LoggingRequest
import com.coach.flame.aspect.LoggingResponse
import com.coach.flame.dailyTask.DailyTaskService
import com.coach.flame.dailyTask.domain.ClientDto
import com.coach.flame.dailyTask.domain.DailyTaskDto
import com.coach.flame.date.stringToDate
import com.coach.flame.exception.RestInvalidRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.lang.UnsupportedOperationException
import java.util.*

@RestController
@RequestMapping("/api/dailyTask")
class DailyTaskImpl(
    @Autowired private val dailyTaskService: DailyTaskService
) : DailyTaskAPI {

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/create/tasks")
    @ResponseBody
    override fun createDailyTasks(@RequestBody(required = true) dailyTasks: List<DailyTaskRequest>): DailyTaskResponse {

        if (dailyTasks.isEmpty()) {
            throw RestInvalidRequest("Empty request structure")
        }

        throw UnsupportedOperationException("This is not supported yet")

    }

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/create/task")
    @ResponseBody
    override fun createDailyTask(@RequestBody(required = true) dailyTask: DailyTaskRequest): DailyTaskResponse {

        try {

            checkNotNull(dailyTask.clientIdentifierCreator) { "Missing clientIdentifierCreator param" }
            checkNotNull(dailyTask.clientIdentifierTask) { "Missing clientIdentifierTask param" }

            val dailyTaskDto = DailyTaskDto(
                identifier = UUID.randomUUID(),
                name = dailyTask.name,
                description = dailyTask.description,
                date = stringToDate(dailyTask.date),
                ticked = false,
                createdBy = ClientDto(UUID.fromString(dailyTask.clientIdentifierCreator)),
                owner = ClientDto(UUID.fromString(dailyTask.clientIdentifierTask)),
            )

            val identifier = dailyTaskService.createDailyTask(dailyTaskDto)

            return DailyTaskResponse(
                dailyTasks = setOf(DailyTask(identifier = identifier.toString()))
            )
        } catch (ex: Exception) {
            when (ex) {
                is IllegalArgumentException,
                is IllegalStateException -> {
                    throw RestInvalidRequest(ex)
                }
                else -> throw ex
            }
        }
    }

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/get/task/client/{clientId}")
    @ResponseBody
    override fun getDailyTasksByClient(@PathVariable(required = true) clientId: Long): DailyTaskResponse {

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
    @GetMapping("/get/task/{taskId}")
    override fun getDailyTaskById(@PathVariable(required = true) taskId: Long): DailyTaskResponse {

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
    @DeleteMapping("/delete/task/{taskUuid}")
    override fun deleteDailyTaskById(@PathVariable(required = true) taskUuid: String): DailyTaskResponse {

        try {
            val identifier = UUID.fromString(taskUuid)

            dailyTaskService.deleteDailyTask(identifier)

            return DailyTaskResponse(
                dailyTasks = setOf(
                    DailyTask(
                        identifier = identifier.toString()
                    )
                )
            )
        } catch (ex: Exception) {
            when (ex) {
                is IllegalArgumentException -> {
                    throw RestInvalidRequest(ex)
                }
                else -> throw ex
            }
        }

    }

}