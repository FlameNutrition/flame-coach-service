package com.coach.flame.testing.component.api.coach

import com.coach.flame.jpa.entity.ClientStatus
import com.coach.flame.jpa.entity.maker.ClientBuilder
import com.coach.flame.jpa.entity.maker.ClientMaker
import com.coach.flame.jpa.entity.maker.CoachMaker
import com.coach.flame.testing.assertion.http.ErrorAssert
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

class GetClientsCoachPlusAvailableClientsTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        endpoint = "/api/coach/getClientsCoachPlusClientsAvailable",
        httpMethod = RequestMethod.GET,
        parameters = ["identifier:e59343bc-6563-4488-a77e-112e886c57ae"]
    )
    fun `test get clients from coach plus the clients available for coaching`() {

        // given
        val uuid = UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")
        val client0 = ClientBuilder.maker()
            .but(with(ClientMaker.clientStatus, ClientStatus.ACCEPTED))
            .make()
        val client1 = ClientBuilder.maker()
            .but(with(ClientMaker.clientStatus, ClientStatus.PENDING))
            .make()
        val client2 = ClientBuilder.maker()
            .but(with(ClientMaker.clientStatus, ClientStatus.AVAILABLE))
            .make()
        val client3 = ClientBuilder.maker()
            .but(with(ClientMaker.clientStatus, ClientStatus.AVAILABLE))
            .make()
        val coach = coachMaker
            .but(
                with(CoachMaker.clients, mutableListOf(client0, client1)),
                with(CoachMaker.uuid, uuid)
            )
            .make()

        every { clientRepositoryMock.findClientsForCoach(coach.uuid.toString()) } returns listOf(
            client0,
            client1,
            client2,
            client3
        )
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
        val jsonResponse = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        then(jsonResponse.getAsJsonPrimitive("identifier").asString).isEqualTo(uuid.toString())
        then(jsonResponse.getAsJsonArray("clientsCoach")).hasSize(4)

        val client0Result = jsonResponse.getAsJsonArray("clientsCoach")
            .find { it.asJsonObject.getAsJsonPrimitive("identifier").asString == client0.uuid.toString() }

        then(client0Result).isNotNull
        then(client0Result!!.asJsonObject.getAsJsonPrimitive("firstname").asString).isEqualTo(client0.firstName)
        then(client0Result.asJsonObject.getAsJsonPrimitive("lastname").asString).isEqualTo(client0.lastName)
        then(client0Result.asJsonObject.getAsJsonPrimitive("status").asString).isEqualTo(client0.clientStatus.name)
        then(client0Result.asJsonObject.getAsJsonPrimitive("email").asString).isEqualTo(client0.user.email)
        then(client0Result.asJsonObject.getAsJsonPrimitive("registrationDate").asString).isEqualTo(client0.registrationDate.toString())

    }

    @Test
    @LoadRequest(
        endpoint = "/api/coach/getClientsCoachPlusClientsAvailable",
        httpMethod = RequestMethod.GET,
        parameters = ["identifier:e59343bc-6563-4488-a77e-112e886c57ae"]
    )
    fun `test get clients from coach without clients`() {

        // given
        val uuid = UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")
        val client0 = ClientBuilder.maker()
            .but(with(ClientMaker.clientStatus, ClientStatus.AVAILABLE))
            .make()
        val client1 = ClientBuilder.maker()
            .but(with(ClientMaker.clientStatus, ClientStatus.AVAILABLE))
            .make()
        val coach = coachMaker
            .but(with(CoachMaker.uuid, uuid)).make()

        every { clientRepositoryMock.findClientsForCoach(coach.uuid.toString()) } returns listOf(client0, client1)
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
        val jsonResponse = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        then(jsonResponse.getAsJsonPrimitive("identifier").asString).isEqualTo(uuid.toString())
        then(jsonResponse.getAsJsonArray("clientsCoach")).hasSize(2)

    }

    @Test
    @LoadRequest(
        endpoint = "/api/coach/getClientsCoachPlusClientsAvailable",
        httpMethod = RequestMethod.GET,
        parameters = ["identifier:e59343bc-6563-4488-a77e-112e886c57ae"]
    )
    fun `test get clients from coach is not in the system`() {

        // given
        val uuid = UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")

        every { coachRepositoryMock.findByUuid(uuid) } returns null

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
            .hasErrorMessageTypeEndsWith("CustomerNotFoundException.html")
            .hasErrorMessageTitle("CustomerNotFoundException")
            .hasErrorMessageDetail("Could not find any coach with uuid: e59343bc-6563-4488-a77e-112e886c57ae.")
            .hasErrorMessageStatus("404")
            .hasErrorMessageCode("2001")
            .hasErrorMessageInstance()
            .notHasErrorMessageDebug()
    }


}
