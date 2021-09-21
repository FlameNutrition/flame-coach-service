package com.coach.flame.testing.component.api.dailyTask

import com.coach.flame.jpa.entity.DailyTask
import com.coach.flame.jpa.entity.maker.ClientBuilder
import com.coach.flame.jpa.entity.maker.CoachBuilder
import com.coach.flame.jpa.entity.maker.DailyTaskBuilder
import com.coach.flame.jpa.entity.maker.DailyTaskMaker
import com.coach.flame.testing.assertion.http.ErrorAssert
import com.coach.flame.testing.component.base.BaseComponentTest
import com.coach.flame.testing.framework.JsonBuilder
import com.coach.flame.testing.framework.LoadRequest
import com.natpryce.makeiteasy.MakeItEasy.with
import io.mockk.every
import io.mockk.slot
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.web.bind.annotation.RequestMethod
import java.time.LocalDate
import java.util.*

class DailyTaskUpdateTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/dailyTask/updateDailyTask.json",
        endpoint = "/api/dailyTask/update/task",
        httpMethod = RequestMethod.POST,
        headers = [
            "taskUUID:3c5845f1-4a90-4396-8610-7261761369ae"
        ]
    )
    fun `test update daily task`() {

        // given
        val dailyTask = slot<DailyTask>()
        val taskUUID = UUID.fromString("3c5845f1-4a90-4396-8610-7261761369ae")
        val client = ClientBuilder.default()
        val coach = CoachBuilder.default()
        val task = DailyTaskBuilder.maker()
            .but(
                with(DailyTaskMaker.uuid, taskUUID),
                with(DailyTaskMaker.client, client),
                with(DailyTaskMaker.createdBy, coach)
            )
            .make()

        every { dailyTaskRepositoryMock.findByUuid(taskUUID) } returns task
        every { dailyTaskRepositoryMock.save(capture(dailyTask)) } answers { dailyTask.captured }

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andDo { MockMvcResultHandlers.print() }
            .andDo { MockMvcResultHandlers.log() }
            .andReturn()

        // then
        then(mvnResponse.response).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.OK.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_JSON_VALUE)

        then(dailyTask.isCaptured).isTrue
        then(dailyTask.captured.uuid).isEqualTo(taskUUID)
        then(dailyTask.captured.name).isEqualTo("Drink Tea")
        then(dailyTask.captured.description).isEqualTo("Drink a 1L of Tea")
        then(dailyTask.captured.date).isEqualTo(LocalDate.parse("2020-12-02"))
        then(dailyTask.captured.createdBy).isEqualTo(coach)
        then(dailyTask.captured.ticked).isEqualTo(true)
        then(dailyTask.captured.client).isEqualTo(client)

        val jsonResponse = JsonBuilder.getJsonFromMockClient(mvnResponse.response)
        val dailyTask0 = jsonResponse.getAsJsonArray("dailyTasks").get(0).asJsonObject

        then(dailyTask0.getAsJsonPrimitive("identifier").asString).isNotEmpty
        then(dailyTask0.getAsJsonPrimitive("taskName").asString).isEqualTo("Drink Tea")
        then(dailyTask0.getAsJsonPrimitive("taskDescription").asString).isEqualTo("Drink a 1L of Tea")
        then(dailyTask0.getAsJsonPrimitive("date").asString).isEqualTo("2020-12-02")
        then(dailyTask0.getAsJsonPrimitive("ticked").asBoolean).isTrue

    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/dailyTask/updateDailyTask.json",
        endpoint = "/api/dailyTask/update/task",
        httpMethod = RequestMethod.POST,
        headers = [
            "taskUUID:3c5845f1-4a90-4396-8610-7261762169ae"
        ]
    )
    fun `test update daily task with unlisted task uuid`() {

        // given
        val taskUUID = UUID.fromString("3c5845f1-4a90-4396-8610-7261762169ae")
        every { dailyTaskRepositoryMock.findByUuid(taskUUID) } returns null

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andDo { MockMvcResultHandlers.print() }
            .andDo { MockMvcResultHandlers.log() }
            .andReturn()

        // then
        then(mvnResponse).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.NOT_FOUND.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE)

        val body = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        ErrorAssert.assertThat(body)
            .hasErrorMessageTypeEndsWith("DailyTaskNotFoundException.html")
            .hasErrorMessageTitle("DailyTaskNotFoundException")
            .hasErrorMessageDetail("Daily task not found, please check the identifier.")
            .hasErrorMessageStatus("404")
            .hasErrorMessageCode("4001")
            .hasErrorMessageInstance()
            .notHasErrorMessageDebug()

    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/dailyTask/updateDailyTaskMissingParam.json",
        endpoint = "/api/dailyTask/update/task",
        httpMethod = RequestMethod.POST,
        headers = [
            "taskUUID:3c5845f1-4a90-4396-8610-7261762169ae"
        ]
    )
    fun `test update daily task but missing ticked param`() {

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andDo { MockMvcResultHandlers.print() }
            .andDo { MockMvcResultHandlers.log() }
            .andReturn()

        // then
        then(mvnResponse).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE)

        val body = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        ErrorAssert.assertThat(body)
            .hasErrorMessageTypeEndsWith("RestInvalidRequestException.html")
            .hasErrorMessageTitle("RestInvalidRequestException")
            .hasErrorMessageDetail("ticked is a mandatory parameters")
            .hasErrorMessageStatus("400")
            .hasErrorMessageCode("1001")
            .hasErrorMessageInstance()
            .notHasErrorMessageDebug()

    }

}
