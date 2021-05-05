package com.coach.flame.testing.component.api.dailyTask

import com.coach.flame.jpa.entity.DailyTask
import com.coach.flame.jpa.entity.maker.DailyTaskBuilder
import com.coach.flame.testing.component.base.BaseComponentTest
import com.coach.flame.testing.framework.JsonBuilder
import com.coach.flame.testing.framework.LoadRequest
import io.mockk.every
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.bind.annotation.RequestMethod

class DailyTaskGetUsingFiltersTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/dailyTask/getDailyTasksOneFilter.json",
        endpoint = "/api/dailyTask/get/tasks/filter",
        httpMethod = RequestMethod.POST
    )
    fun `test get daily tasks using one filter`() {

        // given
        val dailyTask0 = DailyTaskBuilder.default()
        val dailyTask1 = DailyTaskBuilder.default()

        every { dailyTaskRepositoryMock.findAll(any<Specification<DailyTask>>()) } returns listOf(dailyTask0,
            dailyTask1)

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andReturn()

        // then
        then(mvnResponse.response).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.OK.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_JSON_VALUE)

        val jsonResponse = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        then(jsonResponse.getAsJsonArray("dailyTasks")).hasSize(2)

        val task0 = jsonResponse.getAsJsonArray("dailyTasks")
            .first { it.asJsonObject.getAsJsonPrimitive("identifier").asString == dailyTask0.uuid.toString() }.asJsonObject

        then(task0.getAsJsonPrimitive("taskName").asString).isEqualTo(dailyTask0.name)
        then(task0.getAsJsonPrimitive("taskDescription").asString).isEqualTo(dailyTask0.description)
        then(task0.getAsJsonPrimitive("date").asString).isEqualTo(dailyTask0.date.toString())
        then(task0.getAsJsonPrimitive("ticked").asBoolean).isEqualTo(dailyTask0.ticked)

    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/dailyTask/getDailyTasksOneFilter.json",
        endpoint = "/api/dailyTask/get/tasks/filter",
        httpMethod = RequestMethod.POST
    )
    fun `test get daily tasks using multiple filters`() {

        // given
        val dailyTask0 = DailyTaskBuilder.default()

        every { dailyTaskRepositoryMock.findAll(any<Specification<DailyTask>>()) } returns listOf(dailyTask0)

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andReturn()

        // then
        then(mvnResponse.response).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.OK.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_JSON_VALUE)

        val jsonResponse = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        then(jsonResponse.getAsJsonArray("dailyTasks")).hasSize(1)

    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/dailyTask/getDailyTasksInvalidFilter.json",
        endpoint = "/api/dailyTask/get/tasks/filter",
        httpMethod = RequestMethod.POST
    )
    fun `test get daily tasks using invalid filters`() {

        // given
        val dailyTask0 = DailyTaskBuilder.default()

        every { dailyTaskRepositoryMock.findAll(any<Specification<DailyTask>>()) } returns listOf(dailyTask0)

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andReturn()

        // then
        then(mvnResponse).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE)

        val body = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        thenErrorMessageType(body).endsWith("RestInvalidRequestException.html")
        thenErrorMessageTitle(body).isEqualTo("RestInvalidRequestException")
        thenErrorMessageDetail(body).contains("INVALID is an invalid filter")
        thenErrorMessageStatus(body).isEqualTo("400")
        thenErrorCode(body).isEqualTo("1001")
        thenErrorMessageInstance(body).isNotEmpty
        thenErrorMessageDebug(body).isEmpty()

    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/dailyTask/getDailyTasksMissingParamsFilter.json",
        endpoint = "/api/dailyTask/get/tasks/filter",
        httpMethod = RequestMethod.POST
    )
    fun `test get daily tasks using missing params filters`() {

        // given
        val dailyTask0 = DailyTaskBuilder.default()

        every { dailyTaskRepositoryMock.findAll(any<Specification<DailyTask>>()) } returns listOf(dailyTask0)

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andReturn()

        // then
        then(mvnResponse).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE)

        val body = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        thenErrorMessageType(body).endsWith("RestException.html")
        thenErrorMessageTitle(body).isEqualTo("RestException")
        thenErrorMessageDetail(body).contains("BETWEEN_DATES has an invalid number of params")
        thenErrorMessageStatus(body).isEqualTo("500")
        thenErrorCode(body).isEqualTo("1000")
        thenErrorMessageInstance(body).isNotEmpty
        thenErrorMessageDebug(body).isEmpty()

    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/dailyTask/getDailyTasksInvalidParamsFilter.json",
        endpoint = "/api/dailyTask/get/tasks/filter",
        httpMethod = RequestMethod.POST
    )
    fun `test get daily tasks using invalid params filters`() {

        // given
        val dailyTask0 = DailyTaskBuilder.default()

        every { dailyTaskRepositoryMock.findAll(any<Specification<DailyTask>>()) } returns listOf(dailyTask0)

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andReturn()

        // then
        then(mvnResponse).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE)

        val body = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        thenErrorMessageType(body).endsWith("RestInvalidRequestException.html")
        thenErrorMessageTitle(body).isEqualTo("RestInvalidRequestException")
        thenErrorMessageDetail(body).contains("IDENTIFIER is an invalid filter")
        thenErrorMessageStatus(body).isEqualTo("400")
        thenErrorCode(body).isEqualTo("1001")
        thenErrorMessageInstance(body).isNotEmpty
        thenErrorMessageDebug(body).isEmpty()

    }


}
