package com.coach.flame.testing.component.coach

import com.coach.flame.jpa.entity.ClientMaker
import com.coach.flame.jpa.entity.CoachMaker
import com.coach.flame.jpa.entity.UserMaker
import com.coach.flame.jpa.entity.UserSessionMaker
import com.coach.flame.testing.component.base.BaseComponentTest
import com.coach.flame.testing.framework.JsonBuilder
import com.coach.flame.testing.framework.LoadRequest
import com.natpryce.makeiteasy.MakeItEasy
import com.natpryce.makeiteasy.MakeItEasy.*
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.BDDAssertions.then
import org.junit.FixMethodOrder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.web.bind.annotation.RequestMethod
import java.sql.SQLException
import java.time.LocalDateTime
import java.util.*

class GetClientsCoachTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/coach/getClients.json",
        endpoint = "/api/coach/getClients",
        httpMethod = RequestMethod.GET
    )
    fun `test get clients from coach`() {

        // given
        val uuid = UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")
        val coach = coachMaker
            .but(with(CoachMaker.clients, listOf(clientMaker.make(), clientMaker.make())),
                with(CoachMaker.uuid, uuid))
            .make()

        every { coachRepositoryMock.findByUuid(uuid) } returns coach

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andDo { MockMvcResultHandlers.print() }
            .andDo { MockMvcResultHandlers.log() }
            .andReturn()

        // then
        then(mvnResponse.response).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.OK.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_JSON_VALUE)
        val jsonResponse = JsonBuilder.getJsonFromString(mvnResponse.response.contentAsString)

        then(jsonResponse.getAsJsonPrimitive("identifier").asString).isEqualTo(uuid.toString())
        then(jsonResponse.getAsJsonArray("clientsCoach")).hasSize(2)

    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/coach/getClients.json",
        endpoint = "/api/coach/getClients",
        httpMethod = RequestMethod.GET
    )
    fun `test get clients from coach but occurred an internal exception`() {

        val uuid = UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")

        every { coachRepositoryMock.findByUuid(uuid) } throws Exception("Ops...something is wrong!")

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

}