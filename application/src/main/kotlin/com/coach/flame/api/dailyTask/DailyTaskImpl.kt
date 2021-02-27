package com.coach.flame.api.dailyTask

import com.coach.flame.api.dailyTask.request.DailyTaskRequest
import com.coach.flame.api.dailyTask.response.DailyTask
import com.coach.flame.api.dailyTask.response.DailyTaskResponse
import com.coach.flame.aspect.LoggingRequest
import com.coach.flame.aspect.LoggingResponse
import com.coach.flame.dailyTask.DailyTaskService
import com.coach.flame.date.stringToDate
import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.DailyTaskDto
import com.coach.flame.exception.RestException
import com.coach.flame.exception.RestInvalidRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
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
    @PostMapping("/create/tasks")
    @ResponseBody
    override fun createDailyTasks(@RequestBody(required = true) dailyTasks: List<DailyTaskRequest>): DailyTaskResponse {

        if (dailyTasks.isEmpty()) {
            throw RestInvalidRequest("Empty request structure")
        }

        throw RestException("This is not supported yet")

    }

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/create/task")
    @ResponseBody
    override fun createDailyTask(@RequestBody(required = true) dailyTask: DailyTaskRequest): DailyTaskResponse {

        try {

            requireNotNull(dailyTask.name) { "Missing name param" }
            requireNotNull(dailyTask.description) { "Missing description param" }
            requireNotNull(dailyTask.date) { "Missing date param" }
            requireNotNull(dailyTask.clientIdentifierCreator) { "Missing clientIdentifierCreator param" }
            requireNotNull(dailyTask.clientIdentifierTask) { "Missing clientIdentifierTask param" }

            val dailyTaskDto = DailyTaskDto(
                identifier = UUID.randomUUID(),
                name = dailyTask.name,
                description = dailyTask.description,
                date = stringToDate(dailyTask.date),
                ticked = false,
                createdBy = UUID.fromString(dailyTask.clientIdentifierCreator),
                owner = UUID.fromString(dailyTask.clientIdentifierTask),
            )

            val identifier = dailyTaskService.createDailyTask(dailyTaskDto)

            return DailyTaskResponse(
                dailyTasks = setOf(DailyTask(identifier = identifier.toString()))
            )
        } catch (ex: Exception) {
            when (ex) {
                is IllegalArgumentException,
                is IllegalStateException -> {
                    LOGGER.warn("opr=createDailyTask, msg='Invalid request'", ex)
                    throw RestInvalidRequest(ex)
                }
                else -> {
                    LOGGER.error("opr=createDailyTask, msg='Something unexpected happened!'", ex)
                    throw RestException(ex)
                }
            }
        }
    }

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/get/task/client/{clientId}")
    @ResponseBody
    override fun getDailyTasksByClient(@PathVariable(required = true) clientId: Long): DailyTaskResponse {

        try {

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
        } catch (ex: Exception) {
            LOGGER.error("opr=getDailyTasksByClient, msg='Something unexpected happened!'", ex)
            throw RestException(ex)
        }
    }

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/get/task/{taskId}")
    override fun getDailyTaskById(@PathVariable(required = true) taskId: Long): DailyTaskResponse {

        try {
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
        } catch (ex: Exception) {
            LOGGER.error("opr=getDailyTaskById, msg='Something unexpected happened!'", ex)
            throw RestException(ex)
        }
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
                    LOGGER.warn("opr=deleteDailyTaskById, msg='Invalid request'", ex)
                    throw RestInvalidRequest(ex)
                }
                else -> {
                    LOGGER.error("opr=deleteDailyTaskById, msg='Something unexpected happened!'", ex)
                    throw RestException(ex)
                }
            }
        }

    }

}