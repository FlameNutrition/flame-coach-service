package com.coach.flame.testing.component.api.dailyTask

import com.coach.flame.jpa.entity.*
import com.coach.flame.testing.component.base.BaseComponentTest
import com.coach.flame.testing.framework.JsonBuilder
import com.coach.flame.testing.framework.LoadRequest
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
import java.sql.SQLException
import java.util.*

class DailyTaskCreateTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/dailyTask/createNewDailyTask.json",
        endpoint = "/api/dailyTask/create/task",
        httpMethod = RequestMethod.POST,
        headers = [
            "clientIdentifier:3c5845f1-4a90-4396-8610-7261761369ae",
            "coachIdentifier:b2957c86-e493-4f9a-a277-2e24b77f0ffe"
        ]
    )
    fun `test create daily task`() {

        // given
        val dailyTaskSlot = slot<DailyTask>()
        val clientUUID = UUID.fromString("3c5845f1-4a90-4396-8610-7261761369ae")
        val coachUUID = UUID.fromString("b2957c86-e493-4f9a-a277-2e24b77f0ffe")
        val coach = CoachBuilder.default()
        val client = ClientBuilder.default()

        every { coachRepositoryMock.findByUuid(coachUUID) } returns coach
        every { clientRepositoryMock.findByUuid(clientUUID) } returns client
        every { dailyTaskRepositoryMock.save(capture(dailyTaskSlot)) } answers { dailyTaskSlot.captured }

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andDo { MockMvcResultHandlers.print() }
            .andDo { MockMvcResultHandlers.log() }
            .andReturn()

        // then
        then(mvnResponse.response).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.OK.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_JSON_VALUE)

        then(dailyTaskSlot.isCaptured).isTrue
        then(dailyTaskSlot.captured.name).isEqualTo("Drink Water")
        then(dailyTaskSlot.captured.description).isEqualTo("Drink a 1L of water")
        then(dailyTaskSlot.captured.createdBy).isEqualTo(coach)
        then(dailyTaskSlot.captured.client).isEqualTo(client)

        val jsonResponse = JsonBuilder.getJsonFromMockClient(mvnResponse.response)
        val dailyTask0 = jsonResponse.getAsJsonArray("dailyTasks").get(0).asJsonObject

        then(dailyTask0.getAsJsonPrimitive("identifier").asString).isNotEmpty
        then(dailyTask0.getAsJsonPrimitive("taskName").asString).isEqualTo("Drink Water")
        then(dailyTask0.getAsJsonPrimitive("taskDescription").asString).isEqualTo("Drink a 1L of water")
        then(dailyTask0.getAsJsonPrimitive("date").asString).isEqualTo("2020-12-05")
        then(dailyTask0.getAsJsonPrimitive("ticked").asBoolean).isFalse

    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/dailyTask/createNewDailyTask.json",
        endpoint = "/api/dailyTask/create/task",
        httpMethod = RequestMethod.POST,
        headers = [
            "clientIdentifier:3c5845f1-4a90-4396-8610-7261761369ae",
            "coachIdentifier:b2957c86-e493-4f9a-a277-2e24b77f0ffe"
        ]
    )
    fun `test create daily task but throws unexpected exception`() {

        // given
        val dailyTaskSlot = slot<DailyTask>()
        val clientUUID = UUID.fromString("3c5845f1-4a90-4396-8610-7261761369ae")
        val coachUUID = UUID.fromString("b2957c86-e493-4f9a-a277-2e24b77f0ffe")
        val coach = CoachBuilder.default()
        val client = ClientBuilder.default()

        every { coachRepositoryMock.findByUuid(coachUUID) } returns coach
        every { clientRepositoryMock.findByUuid(clientUUID) } returns client
        every { dailyTaskRepositoryMock.save(capture(dailyTaskSlot)) } throws SQLException("This is a sensible information")

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andDo { MockMvcResultHandlers.print() }
            .andDo { MockMvcResultHandlers.log() }
            .andReturn()

        // then
        then(mvnResponse).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE)

        val body = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        thenErrorMessageType(body).endsWith("InternalServerException.html")
        thenErrorMessageTitle(body).isEqualTo("InternalServerException")
        thenErrorMessageDetail(body).isEqualTo("This is an internal problem, please contact the admin system")
        thenErrorMessageStatus(body).isEqualTo("500")
        thenErrorMessageInstance(body).isNotEmpty
        thenErrorMessageDebug(body).isEmpty()

    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/dailyTask/createNewDailyTask.json",
        endpoint = "/api/dailyTask/create/task",
        headers = [
            "clientIdentifier:3c5845f1-4a90-4396-8610-7261761369ae",
            "coachIdentifier:7b1b4b46-be74-4779-ade8-a0a1a6968ca0"
        ],
        httpMethod = RequestMethod.POST
    )
    fun `test create daily task with invalid coachIdentifier`() {

        // given
        val coachUUID = UUID.fromString("7b1b4b46-be74-4779-ade8-a0a1a6968ca0")
        every { coachRepositoryMock.findByUuid(coachUUID) } returns null

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

        thenErrorMessageType(body).endsWith("ClientNotFoundException.html")
        thenErrorMessageTitle(body).isEqualTo("ClientNotFoundException")
        thenErrorMessageDetail(body).isEqualTo("Didn't find any coach with this identifier, please check the coach identifier.")
        thenErrorMessageStatus(body).isEqualTo("404")
        thenErrorMessageInstance(body).isNotEmpty
        thenErrorMessageDebug(body).isEmpty()

    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/dailyTask/createNewDailyTask.json",
        endpoint = "/api/dailyTask/create/task",
        httpMethod = RequestMethod.POST,
        headers = [
            "clientIdentifier:3c5845f1-4a90-4396-8610-7261761369ae",
            "coachIdentifier:b2957c86-e493-4f9a-a277-2e24b77f0ffe"
        ]
    )
    fun `test create daily task with invalid clientIdentifier`() {

        // given
        val clientUUID = UUID.fromString("3c5845f1-4a90-4396-8610-7261761369ae")
        val coachUUID = UUID.fromString("b2957c86-e493-4f9a-a277-2e24b77f0ffe")
        val coach = CoachBuilder.default()

        every { coachRepositoryMock.findByUuid(coachUUID) } returns coach
        every { clientRepositoryMock.findByUuid(clientUUID) } returns null

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

        thenErrorMessageType(body).endsWith("ClientNotFoundException.html")
        thenErrorMessageTitle(body).isEqualTo("ClientNotFoundException")
        thenErrorMessageDetail(body).isEqualTo("Didn't find any client with this identifier, please check the client identifier.")
        thenErrorMessageStatus(body).isEqualTo("404")
        thenErrorMessageInstance(body).isNotEmpty
        thenErrorMessageDebug(body).isEmpty()

    }

}
