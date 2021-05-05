package com.coach.flame.testing.component.api.dailyTask

import com.coach.flame.jpa.entity.maker.DailyTaskBuilder
import com.coach.flame.jpa.entity.maker.DailyTaskMaker
import com.coach.flame.testing.component.base.BaseComponentTest
import com.coach.flame.testing.framework.JsonBuilder
import com.coach.flame.testing.framework.LoadRequest
import com.natpryce.makeiteasy.MakeItEasy.with
import io.mockk.every
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.web.bind.annotation.RequestMethod
import java.util.*

class DailyTaskGetClientTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        endpoint = "/api/dailyTask/get/tasks/client",
        httpMethod = RequestMethod.GET,
        headers = [
            "clientIdentifier:3c5845f1-4a90-4396-8610-7261761369ae",
        ]
    )
    fun `test get daily tasks from client`() {

        // given
        val clientUUID = UUID.fromString("3c5845f1-4a90-4396-8610-7261761369ae")
        val dailyTask0 = DailyTaskBuilder.maker()
            .but(with(DailyTaskMaker.description, "What’s not to like? Custard – good. Jam – good. Meat – good!"))
            .make()
        val dailyTask1 = DailyTaskBuilder.default()

        every { dailyTaskRepositoryMock.findAllByClient(clientUUID) } returns Optional.of(setOf(dailyTask0, dailyTask1))

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
        endpoint = "/api/dailyTask/get/tasks/client",
        httpMethod = RequestMethod.GET,
        headers = [
            "clientIdentifier:3c5845f1-4a90-4396-8610-7261761369ae",
        ]
    )
    fun `test get daily tasks empty from client`() {

        // given
        val clientUUID = UUID.fromString("3c5845f1-4a90-4396-8610-7261761369ae")

        every { dailyTaskRepositoryMock.findAllByClient(clientUUID) } returns Optional.empty()

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andDo { MockMvcResultHandlers.print() }
            .andDo { MockMvcResultHandlers.log() }
            .andReturn()

        // then
        then(mvnResponse.response).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.OK.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_JSON_VALUE)

        val jsonResponse = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        then(jsonResponse.getAsJsonArray("dailyTasks")).hasSize(0)

    }


}
