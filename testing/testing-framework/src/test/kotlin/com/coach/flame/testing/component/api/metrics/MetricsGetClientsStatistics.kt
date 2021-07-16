package com.coach.flame.testing.component.api.metrics

import com.coach.flame.failure.exception.CustomerNotFoundException
import com.coach.flame.jpa.entity.ClientStatus
import com.coach.flame.jpa.entity.maker.ClientBuilder
import com.coach.flame.jpa.entity.maker.ClientMaker
import com.coach.flame.jpa.entity.maker.CoachBuilder
import com.coach.flame.jpa.entity.maker.CoachMaker
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

class MetricsGetClientsStatistics : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        endpoint = "/api/metrics/clients",
        httpMethod = RequestMethod.GET,
        parameters = [
            "coachIdentifier:3c5845f1-4a90-4396-8610-7261761369ae"
        ]
    )
    fun `test get clients metrics from coach`() {

        // given
        val client1 = ClientBuilder.maker()
            .but(with(ClientMaker.clientStatus, ClientStatus.PENDING))
            .make()
        val client2 = ClientBuilder.maker()
            .but(with(ClientMaker.clientStatus, ClientStatus.PENDING))
            .make()
        val client3 = ClientBuilder.maker()
            .but(with(ClientMaker.clientStatus, ClientStatus.ACCEPTED))
            .make()
        val listOfClients = mutableListOf(client1, client2, client3)

        val coach = CoachBuilder.maker()
            .but(with(CoachMaker.clients, listOfClients),
                with(CoachMaker.uuid, UUID.fromString("3c5845f1-4a90-4396-8610-7261761369ae")))
            .make()

        every {
            coachOperationsMock.getCoach(UUID.fromString("3c5845f1-4a90-4396-8610-7261761369ae"))
        } returns coach

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

        then(jsonResponse.getAsJsonPrimitive("coachIdentifier").asString).isEqualTo(coach.uuid.toString())

        then(jsonResponse.getAsJsonObject("clientsStatus").get("numberOfClientsAccepted").asInt).isEqualTo(1)
        then(jsonResponse.getAsJsonObject("clientsStatus").get("numberOfClientsPending").asInt).isEqualTo(2)
        then(jsonResponse.getAsJsonObject("clientsStatus").get("numberOfTotalClients").asInt).isEqualTo(3)

    }

    @Test
    @LoadRequest(
        endpoint = "/api/metrics/clients",
        httpMethod = RequestMethod.GET,
        parameters = [
            "coachIdentifier:3c5845f1-4a90-4396-8610-7261761369a7"
        ]
    )
    fun `test get clients metrics from invalid coach`() {

        every {
            coachOperationsMock.getCoach(UUID.fromString("3c5845f1-4a90-4396-8610-7261761369a7"))
        } throws CustomerNotFoundException("Could not find any coach with uuid: 3c5845f1-4a90-4396-8610-7261761369a7")

        // when
        val mvnResponse = mockMvc.perform(request!!)
            .andDo { MockMvcResultHandlers.print() }
            .andDo { MockMvcResultHandlers.log() }
            .andReturn()

        // then
        then(mvnResponse.response).isNotNull
        then(mvnResponse.response.status).isEqualTo(HttpStatus.NOT_FOUND.value())
        then(mvnResponse.response.contentType).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE)

        val body = JsonBuilder.getJsonFromMockClient(mvnResponse.response)

        thenErrorMessageType(body).endsWith("CustomerNotFoundException.html")
        thenErrorMessageTitle(body).isEqualTo("CustomerNotFoundException")
        thenErrorMessageDetail(body).contains("Could not find any coach with uuid: 3c5845f1-4a90-4396-8610-7261761369a7")
        thenErrorMessageStatus(body).isEqualTo("404")
        thenErrorCode(body).isEqualTo("2001")
        thenErrorMessageInstance(body).isNotEmpty
        thenErrorMessageDebug(body).isEmpty()

    }

}
