package com.coach.flame.api.dailyTask

import com.coach.flame.api.dailyTask.request.DailyTaskFilter
import com.coach.flame.api.dailyTask.request.DailyTaskFiltersRequest
import com.coach.flame.api.dailyTask.request.DailyTaskRequest
import com.coach.flame.api.dailyTask.response.DailyTask
import com.coach.flame.api.dailyTask.response.DailyTaskResponse
import com.coach.flame.aspect.LoggingRequest
import com.coach.flame.aspect.LoggingResponse
import com.coach.flame.dailyTask.DailyTaskService
import com.coach.flame.dailyTask.filter.BetweenDatesFilter
import com.coach.flame.dailyTask.filter.IdentifierFilter
import com.coach.flame.date.stringToDate
import com.coach.flame.domain.DailyTaskDto
import com.coach.flame.exception.RestException
import com.coach.flame.exception.RestInvalidRequestException
import com.coach.flame.failure.domain.ErrorCode
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

        private val converter = { task: DailyTaskDto ->
            DailyTask(
                identifier = task.identifier.toString(),
                taskName = task.name,
                taskDescription = task.description,
                date = task.date.toString(),
                ticked = task.ticked
            )
        }
    }

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/create/task")
    @ResponseBody
    override fun createDailyTask(
        @RequestHeader("clientIdentifier") clientIdentifier: UUID,
        @RequestHeader("coachIdentifier") coachIdentifier: UUID,
        @RequestBody(required = true) dailyTask: DailyTaskRequest,
    ): DailyTaskResponse {

        try {

            requireNotNull(dailyTask.taskName) { "Missing taskName param" }
            requireNotNull(dailyTask.taskDescription) { "Missing taskDescription param" }
            requireNotNull(dailyTask.date) { "Missing date param" }

            val dailyTaskDto = DailyTaskDto(
                identifier = UUID.randomUUID(),
                name = dailyTask.taskName,
                description = dailyTask.taskDescription,
                date = stringToDate(dailyTask.date),
                ticked = false,
                clientIdentifier = clientIdentifier,
                coachIdentifier = coachIdentifier,
                coach = null,
                client = null
            )

            val dailyTasksList = if (dailyTask.toDate !== null) {
                dailyTaskService.createDailyTask(dailyTaskDto, stringToDate(dailyTask.toDate))
            } else {
                setOf(dailyTaskService.createDailyTask(dailyTaskDto))
            }

            return DailyTaskResponse(dailyTasks = dailyTasksList.map { converter(it) }.toSet())
        } catch (ex: Exception) {
            when (ex) {
                is IllegalArgumentException -> {
                    LOGGER.warn("opr='createDailyTask', msg='Invalid request'", ex)
                    throw RestInvalidRequestException(ex.localizedMessage, ex)
                }
                is IllegalStateException -> {
                    LOGGER.warn("opr='createDailyTask', msg='Please check following problem'", ex)
                    throw RestException(ErrorCode.CODE_1000, ex.localizedMessage, ex)
                }
                else -> {
                    throw ex
                }
            }
        }
    }

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/get/tasks/client")
    @ResponseBody
    override fun getDailyTasksByClient(@RequestHeader(required = true) clientIdentifier: UUID): DailyTaskResponse {

        val dailyTasksDto = dailyTaskService.getDailyTasksByClient(clientIdentifier)

        return DailyTaskResponse(
            dailyTasks = dailyTasksDto.map { converter(it) }.toSet()
        )
    }

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/get/tasks/filter")
    @ResponseBody
    override fun getDailyTasksUsingFilters(@RequestBody(required = true) request: DailyTaskFiltersRequest): DailyTaskResponse {

        try {

            check(request.filters.isNotEmpty()) { "filters can not be empty" }

            val listOfFilters = request.filters
                .map {

                    try {
                        val filterType = DailyTaskFilter.Filter.valueOf(it.type)
                        it.verifyNumOfParams()

                        when (filterType) {
                            DailyTaskFilter.Filter.IDENTIFIER -> {
                                IdentifierFilter(UUID.fromString(it.values.first()))
                            }
                            DailyTaskFilter.Filter.BETWEEN_DATES -> {
                                BetweenDatesFilter(stringToDate(it.values.first()), stringToDate(it.values.last()))
                            }
                        }
                    } catch (ex: IllegalArgumentException) {
                        throw IllegalArgumentException("${it.type} is an invalid filter", ex)
                    }
                }
                .toSet()

            val dailyTasks = dailyTaskService.getDailyTasksUsingFilters(listOfFilters)

            return DailyTaskResponse(dailyTasks = dailyTasks.map { converter(it) }.toSet())

        } catch (ex: Exception) {
            when (ex) {
                is IllegalArgumentException -> {
                    LOGGER.warn("opr='getDailyTasksUsingFilters', msg='Invalid request'", ex)
                    throw RestInvalidRequestException(ex.localizedMessage, ex)
                }
                is IllegalStateException -> {
                    LOGGER.warn("opr='getDailyTasksUsingFilters', msg='Invalid request'", ex)
                    throw RestException(ErrorCode.CODE_1000, ex.localizedMessage, ex)
                }
                else -> {
                    throw ex
                }
            }
        }
    }

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/update/task")
    @ResponseBody
    override fun updateDailyTask(
        @RequestHeader("taskUUID") taskUUID: UUID,
        @RequestBody(required = true) request: DailyTaskRequest,
    ): DailyTaskResponse {

        try {

            requireNotNull(request.taskName) { "taskName is a mandatory parameters" }
            requireNotNull(request.taskDescription) { "taskDescription is a mandatory parameters" }
            requireNotNull(request.ticked) { "ticked is a mandatory parameters" }
            requireNotNull(request.date) { "date is a mandatory parameters" }

            val dailyTaskDto = DailyTaskDto(
                identifier = taskUUID,
                name = request.taskName,
                description = request.taskDescription,
                date = stringToDate(request.date),
                ticked = request.ticked,
                clientIdentifier = null,
                coachIdentifier = null,
                client = null,
                coach = null
            )

            val createdDailyTask = dailyTaskService.updateDailyTask(dailyTaskDto)

            return DailyTaskResponse(
                dailyTasks = setOf(converter(createdDailyTask))
            )
        } catch (ex: Exception) {
            when (ex) {
                is IllegalArgumentException -> {
                    LOGGER.warn("opr='updateDailyTask', msg='Invalid request'", ex)
                    throw RestInvalidRequestException(ex.localizedMessage, ex)
                }
                is IllegalStateException -> {
                    LOGGER.warn("opr='updateDailyTask', msg='Please check following problem'", ex)
                    throw RestException(ErrorCode.CODE_1000, ex.localizedMessage, ex)
                }
                else -> {
                    throw ex
                }
            }
        }
    }

    @LoggingRequest
    @LoggingResponse
    @DeleteMapping("/delete/task")
    @ResponseBody
    override fun deleteDailyTask(@RequestHeader(required = true) taskUUID: String): DailyTaskResponse {

        try {
            val identifier = UUID.fromString(taskUUID)

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
                    throw RestInvalidRequestException(ex.localizedMessage, ex)
                }
                is IllegalStateException -> {
                    LOGGER.warn("opr='deleteDailyTaskById', msg='Please check following problem'", ex)
                    throw RestException(ErrorCode.CODE_1000, ex.localizedMessage, ex)
                }
                else -> {
                    throw ex
                }
            }
        }

    }

}
