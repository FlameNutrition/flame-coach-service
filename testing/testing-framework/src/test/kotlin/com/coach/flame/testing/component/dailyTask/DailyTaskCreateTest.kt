package com.coach.flame.testing.component.dailyTask

import com.coach.flame.jpa.entity.DailyTask
import com.coach.flame.jpa.entity.UserMaker
import com.coach.flame.jpa.entity.UserSessionMaker
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
            "clientToken:3c5845f1-4a90-4396-8610-7261761369ae",
            "coachToken:b2957c86-e493-4f9a-a277-2e24b77f0ffe"
        ]
    )
    fun `test create daily task`() {

        // given
        val dailyTaskSlot = slot<DailyTask>()
        val clientToken = UUID.fromString("3c5845f1-4a90-4396-8610-7261761369ae")
        val coachToken = UUID.fromString("b2957c86-e493-4f9a-a277-2e24b77f0ffe")
        val coachClient = clientMaker.make()
        val userCoach = userMaker
            .but(with(UserMaker.client, coachClient))
            .make()
        val coachSession = userSessionMaker
            .but(with(UserSessionMaker.token, coachToken),
                with(UserSessionMaker.user, userCoach))
            .make()
        val client = clientMaker
            .make()

        every { userSessionRepositoryMock.findByToken(coachToken) } returns coachSession
        every { clientRepositoryMock.findByUuid(clientToken) } returns client
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
        then(dailyTaskSlot.captured.createdBy).isEqualTo(coachSession.user?.client)
        then(dailyTaskSlot.captured.client).isEqualTo(client)

        val jsonResponse = JsonBuilder.getJsonFromString(mvnResponse.response.contentAsString)
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
            "clientToken:3c5845f1-4a90-4396-8610-7261761369ae",
            "coachToken:b2957c86-e493-4f9a-a277-2e24b77f0ffe"
        ]
    )
    fun `test create daily task but throws unexpected exception`() {

        // given
        val dailyTaskSlot = slot<DailyTask>()
        val clientToken = UUID.fromString("3c5845f1-4a90-4396-8610-7261761369ae")
        val coachToken = UUID.fromString("b2957c86-e493-4f9a-a277-2e24b77f0ffe")
        val coachSession = userSessionMaker
            .but(with(UserSessionMaker.token, coachToken))
            .make()
        val client = clientMaker
            .make()

        every { userSessionRepositoryMock.findByToken(coachToken) } returns coachSession
        every { clientRepositoryMock.findByUuid(clientToken) } returns client
        every { dailyTaskRepositoryMock.saveAndFlush(capture(dailyTaskSlot)) } throws SQLException("This is a sensible information")

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andDo { MockMvcResultHandlers.print() }
            .andDo { MockMvcResultHandlers.log() }
            .andReturn()

        // then
        then(mvnResponse).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE)

        val body = JsonBuilder.getJsonFromString(mvnResponse.response.contentAsString)

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
            "clientToken:3c5845f1-4a90-4396-8610-7261761369ae",
            "coachToken:7b1b4b46-be74-4779-ade8-a0a1a6968ca0"
        ],
        httpMethod = RequestMethod.POST
    )
    fun `test create daily task with invalid coachToken`() {

        // given
        val coachToken = UUID.fromString("7b1b4b46-be74-4779-ade8-a0a1a6968ca0")
        every { userSessionRepositoryMock.findByToken(coachToken) } returns null

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andDo { MockMvcResultHandlers.print() }
            .andDo { MockMvcResultHandlers.log() }
            .andReturn()

        // then
        then(mvnResponse).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.NOT_FOUND.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE)

        val body = JsonBuilder.getJsonFromString(mvnResponse.response.contentAsString)

        thenErrorMessageType(body).endsWith("ClientNotFoundException.html")
        thenErrorMessageTitle(body).isEqualTo("ClientNotFoundException")
        thenErrorMessageDetail(body).isEqualTo("Didn't find any coach session, please check the coachToken identifier.")
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
            "clientToken:3c5845f1-4a90-4396-8610-7261761369ae",
            "coachToken:b2957c86-e493-4f9a-a277-2e24b77f0ffe"
        ]
    )
    fun `test create daily task with invalid clientToken`() {

        // given
        val clientToken = UUID.fromString("3c5845f1-4a90-4396-8610-7261761369ae")
        val coachToken = UUID.fromString("b2957c86-e493-4f9a-a277-2e24b77f0ffe")
        val coachSession = userSessionMaker
            .but(with(UserSessionMaker.token, coachToken))
            .make()

        every { userSessionRepositoryMock.findByToken(coachToken) } returns coachSession
        every { clientRepositoryMock.findByUuid(clientToken) } returns null

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andDo { MockMvcResultHandlers.print() }
            .andDo { MockMvcResultHandlers.log() }
            .andReturn()

        // then
        then(mvnResponse).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.NOT_FOUND.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE)

        val body = JsonBuilder.getJsonFromString(mvnResponse.response.contentAsString)

        thenErrorMessageType(body).endsWith("ClientNotFoundException.html")
        thenErrorMessageTitle(body).isEqualTo("ClientNotFoundException")
        thenErrorMessageDetail(body).isEqualTo("Didn't find any client with this identifier, please check the client identifier.")
        thenErrorMessageStatus(body).isEqualTo("404")
        thenErrorMessageInstance(body).isNotEmpty
        thenErrorMessageDebug(body).isEmpty()

    }

}