package com.coach.flame.api.dailyTask

import com.coach.flame.api.dailyTask.request.DailyTaskRequest
import com.coach.flame.api.dailyTask.response.DailyTask
import com.coach.flame.api.dailyTask.response.DailyTaskResponse
import com.coach.flame.aspect.LoggingRequest
import com.coach.flame.aspect.LoggingResponse
import com.coach.flame.dailyTask.DailyTaskService
import com.coach.flame.date.stringToDate
import com.coach.flame.domain.DailyTaskDto
import com.coach.flame.exception.RestException
import com.coach.flame.exception.RestInvalidRequestException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/dailyTask")
class DailyTaskImpl(
    private val dailyTaskService: DailyTaskService,
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
            throw RestInvalidRequestException("Empty request structure")
        }

        throw RestException("This is not supported yet")

    }

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/create/task")
    @ResponseBody
    override fun createDailyTask(
        @RequestHeader("clientToken") clientToken: UUID,
        @RequestHeader("coachToken") coachToken: UUID,
        @RequestBody(required = true) dailyTask: DailyTaskRequest,
    ): DailyTaskResponse {

        try {

            requireNotNull(dailyTask.taskName) { "Missing taskName param" }
            requireNotNull(dailyTask.taskDescription) { "Missing taskDescription param" }
            requireNotNull(dailyTask.date) { "Missing date param" }

            if (dailyTask.toDate !== null) {
                throw UnsupportedOperationException("Multiple daily tasks is not supported")
            }

            val dailyTaskDto = DailyTaskDto(
                identifier = UUID.randomUUID(),
                name = dailyTask.taskName,
                description = dailyTask.taskDescription,
                date = stringToDate(dailyTask.date),
                ticked = false,
                clientIdentifier = clientToken,
                coachIdentifier = coachToken,
            )

            val createdDailyTask = dailyTaskService.createDailyTask(dailyTaskDto)

            return DailyTaskResponse(
                dailyTasks = setOf(
                    DailyTask(
                        identifier = createdDailyTask.identifier.toString(),
                        taskName = createdDailyTask.name,
                        taskDescription = createdDailyTask.description,
                        ticked = createdDailyTask.ticked,
                        date = createdDailyTask.date.toString()
                    ))
            )
        } catch (ex: Exception) {
            when (ex) {
                is IllegalArgumentException -> {
                    LOGGER.warn("opr='createDailyTask', msg='Invalid request'", ex)
                    throw RestInvalidRequestException(ex)
                }
                is IllegalStateException -> {
                    LOGGER.warn("opr='createDailyTask', msg='Please check following problem'", ex)
                    throw RestException(ex)
                }
                else -> {
                    throw ex
                }
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
                        taskName = dto.name,
                        taskDescription = dto.description,
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
                    taskName = dailyTask.name,
                    taskDescription = dailyTask.description,
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
                    LOGGER.warn("opr='deleteDailyTaskById', msg='Invalid request'", ex)
                    throw RestInvalidRequestException(ex)
                }
                is IllegalStateException -> {
                    LOGGER.warn("opr='deleteDailyTaskById', msg='Please check following problem'", ex)
                    throw RestException(ex)
                }
                else -> {
                    throw ex
                }
            }
        }

    }

}