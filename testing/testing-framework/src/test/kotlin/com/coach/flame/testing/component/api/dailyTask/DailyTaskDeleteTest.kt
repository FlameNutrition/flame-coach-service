package com.coach.flame.testing.component.api.dailyTask

import com.coach.flame.jpa.entity.DailyTaskBuilder
import com.coach.flame.jpa.entity.DailyTaskMaker
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
import org.springframework.web.bind.annotation.RequestMethod
import java.util.*

class DailyTaskDeleteTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        endpoint = "/api/dailyTask/delete/task",
        httpMethod = RequestMethod.DELETE,
        headers = [
            "taskUUID:3c5845f1-4a90-4396-8610-7261761369ae",
        ]
    )
    fun `test delete daily tasks by uuid`() {

        // given
        val taskIdentifier = UUID.fromString("3c5845f1-4a90-4396-8610-7261761369ae")
        val dailyTask0 = DailyTaskBuilder.maker()
            .but(with(DailyTaskMaker.uuid, taskIdentifier))
            .make()

        every { dailyTaskRepositoryMock.deleteByUuid(taskIdentifier) } returns 1

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andReturn()

        // then
        then(mvnResponse.response).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.OK.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_JSON_VALUE)

        val jsonResponse = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        then(jsonResponse.getAsJsonArray("dailyTasks")).hasSize(1)

        val task0 = jsonResponse.getAsJsonArray("dailyTasks")
            .first { it.asJsonObject.getAsJsonPrimitive("identifier").asString == dailyTask0.uuid.toString() }.asJsonObject

        then(task0).isNotNull

    }

    @Test
    @LoadRequest(
        endpoint = "/api/dailyTask/delete/task",
        httpMethod = RequestMethod.DELETE,
        headers = [
            "taskUUID:3c5845f1-4a90-4396-8610-7261761369ae",
        ]
    )
    fun `test delete unlisted daily tasks by uuid`() {

        // given
        every { dailyTaskRepositoryMock.deleteByUuid(UUID.fromString("3c5845f1-4a90-4396-8610-7261761369ae")) } returns 0

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andReturn()

        // then
        then(mvnResponse).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE)

        val body = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        thenErrorMessageType(body).endsWith("DailyTaskMissingDeleteException.html")
        thenErrorMessageTitle(body).isEqualTo("DailyTaskMissingDeleteException")
        thenErrorMessageDetail(body).contains("Didn't find the following uuid task: 3c5845f1-4a90-4396-8610-7261761369ae")
        thenErrorMessageStatus(body).isEqualTo("400")
        thenErrorMessageInstance(body).isNotEmpty
        thenErrorMessageDebug(body).isEmpty()

    }


}
