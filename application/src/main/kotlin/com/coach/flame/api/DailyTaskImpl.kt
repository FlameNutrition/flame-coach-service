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

        if (clientId == 100L) {
            val uuid = UUID.randomUUID()

            val t1 = DailyTask(
                identifier = uuid.toString(),
                name = "Drink Water",
                description = "You should drink 2L of water",
                date = LocalDate.now().toString()
            )

            val t2 = DailyTask(
                identifier = uuid.toString(),
                name = "Drink Water",
                description = "You should drink 2L of water",
                date = LocalDate.now().toString()
            )

            return DailyTaskResponse(dailyTasks = setOf(t1, t2))
        } else {
            throw RuntimeException()
        }

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
                    date = dailyTask.date.toString()
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