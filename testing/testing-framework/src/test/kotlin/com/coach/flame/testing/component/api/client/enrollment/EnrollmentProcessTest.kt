package com.coach.flame.testing.component.api.client.enrollment

import com.coach.flame.jpa.entity.*
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
import java.util.*

class EnrollmentProcessTest : BaseComponentTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/client/initEnrollment.json",
        endpoint = "/api/client/enrollment/init",
        httpMethod = RequestMethod.POST,
    )
    fun `test init the enrollment process for a client`() {

        // given
        val uuidClient = UUID.fromString("798cf556-7c23-4637-9aef-a862dc62cba8")
        val uuidCoach = UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")
        val client0 = ClientBuilder.maker()
            .but(with(ClientMaker.clientStatus, ClientStatus.AVAILABLE),
                with(ClientMaker.uuid, uuidClient))
            .make()
        val coach = CoachBuilder.maker()
            .but(with(CoachMaker.uuid, uuidCoach))
            .make()
        val client = slot<Client>()

        every { clientRepositoryMock.findByUuid(uuidClient) } returns client0
        every { coachRepositoryMock.findByUuid(uuidCoach) } returns coach
        every { clientRepositoryMock.save(capture(client)) } answers { client.captured }

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

        then(jsonResponse.getAsJsonObject("coach").asJsonObject.get("identifier").asString).isEqualTo(client.captured.coach?.uuid.toString())
        then(jsonResponse.getAsJsonObject("coach").asJsonObject.get("firstName").asString).isEqualTo(client.captured.coach?.firstName)
        then(jsonResponse.getAsJsonObject("coach").asJsonObject.get("lastName").asString).isEqualTo(client.captured.coach?.lastName)
        then(jsonResponse.getAsJsonPrimitive("client").asString).isEqualTo(client.captured.uuid.toString())
        then(jsonResponse.getAsJsonPrimitive("status").asString).isEqualTo("PENDING")

    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/client/finishEnrollmentAccepted.json",
        endpoint = "/api/client/enrollment/finish",
        httpMethod = RequestMethod.POST,
    )
    fun `test finish accepted enrollment process for a client`() {

        // given
        val uuidClient = UUID.fromString("798cf556-7c23-4637-9aef-a862dc62cba8")
        val coach = CoachBuilder.default()
        val client0 = ClientBuilder.maker()
            .but(with(ClientMaker.clientStatus, ClientStatus.PENDING),
                with(ClientMaker.uuid, uuidClient),
                with(ClientMaker.coach, coach))
            .make()

        val client = slot<Client>()

        every { clientRepositoryMock.findByUuid(uuidClient) } returns client0
        every { clientRepositoryMock.save(capture(client)) } answers { client.captured }

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

        then(jsonResponse.getAsJsonObject("coach").asJsonObject.get("identifier").asString).isEqualTo(client.captured.coach?.uuid.toString())
        then(jsonResponse.getAsJsonObject("coach").asJsonObject.get("firstName").asString).isEqualTo(client.captured.coach?.firstName)
        then(jsonResponse.getAsJsonObject("coach").asJsonObject.get("lastName").asString).isEqualTo(client.captured.coach?.lastName)
        then(jsonResponse.getAsJsonPrimitive("client").asString).isEqualTo(client.captured.uuid.toString())
        then(jsonResponse.getAsJsonPrimitive("status").asString).isEqualTo("ACCEPTED")

    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/client/finishEnrollmentNotAccepted.json",
        endpoint = "/api/client/enrollment/finish",
        httpMethod = RequestMethod.POST,
    )
    fun `test finish not accepted enrollment process for a client`() {

        // given
        val uuidClient = UUID.fromString("798cf556-7c23-4637-9aef-a862dc62cba8")
        val coach = CoachBuilder.default()
        val client0 = ClientBuilder.maker()
            .but(with(ClientMaker.clientStatus, ClientStatus.PENDING),
                with(ClientMaker.uuid, uuidClient),
                with(ClientMaker.coach, coach))
            .make()

        val client = slot<Client>()

        every { clientRepositoryMock.findByUuid(uuidClient) } returns client0
        every { clientRepositoryMock.save(capture(client)) } answers { client.captured }

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

        then(jsonResponse.get("coach").isJsonNull).isTrue
        then(jsonResponse.getAsJsonPrimitive("client").asString).isEqualTo(client.captured.uuid.toString())
        then(jsonResponse.getAsJsonPrimitive("status").asString).isEqualTo("AVAILABLE")

    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/component/client/breakEnrollment.json",
        endpoint = "/api/client/enrollment/break",
        httpMethod = RequestMethod.POST,
    )
    fun `test break enrollment between client and coach`() {

        // given
        val uuidClient = UUID.fromString("798cf556-7c23-4637-9aef-a862dc62cba8")
        val coach = CoachBuilder.default()
        val client0 = ClientBuilder.maker()
            .but(with(ClientMaker.clientStatus, ClientStatus.ACCEPTED),
                with(ClientMaker.uuid, uuidClient),
                with(ClientMaker.coach, coach))
            .make()

        val client = slot<Client>()

        every { clientRepositoryMock.findByUuid(uuidClient) } returns client0
        every { clientRepositoryMock.save(capture(client)) } answers { client.captured }

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

        then(jsonResponse.get("coach").isJsonNull).isTrue
        then(jsonResponse.getAsJsonPrimitive("client").asString).isEqualTo(client.captured.uuid.toString())
        then(jsonResponse.getAsJsonPrimitive("status").asString).isEqualTo("AVAILABLE")

    }

    @LoadRequest(
        endpoint = "/api/client/enrollment/status",
        httpMethod = RequestMethod.GET,
        headers = ["clientUUID:798cf556-7c23-4637-9aef-a862dc62cba8"]
    )
    fun `test get status (accepted) enrollment between client and coach`() {

        // given
        val uuidClient = UUID.fromString("798cf556-7c23-4637-9aef-a862dc62cba8")
        val coach = CoachBuilder.default()
        val client0 = ClientBuilder.maker()
            .but(with(ClientMaker.clientStatus, ClientStatus.ACCEPTED),
                with(ClientMaker.uuid, uuidClient),
                with(ClientMaker.coach, coach))
            .make()

        val client = slot<Client>()

        every { clientRepositoryMock.findByUuid(uuidClient) } returns client0

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

        then(jsonResponse.getAsJsonObject("coach").asJsonObject.get("identifier").asString).isEqualTo(client.captured.coach?.uuid.toString())
        then(jsonResponse.getAsJsonObject("coach").asJsonObject.get("firstName").asString).isEqualTo(client.captured.coach?.firstName)
        then(jsonResponse.getAsJsonObject("coach").asJsonObject.get("lastName").asString).isEqualTo(client.captured.coach?.lastName)
        then(jsonResponse.getAsJsonPrimitive("client").asString).isEqualTo(client.captured.uuid.toString())
        then(jsonResponse.getAsJsonPrimitive("status").asString).isEqualTo("ACCEPTED")

    }

    @LoadRequest(
        endpoint = "/api/client/enrollment/status",
        httpMethod = RequestMethod.GET,
        headers = ["clientUUID:798cf556-7c23-4637-9aef-a862dc62cba8"]
    )
    fun `test get status (available) enrollment between client and coach`() {

        // given
        val uuidClient = UUID.fromString("798cf556-7c23-4637-9aef-a862dc62cba8")
        val client0 = ClientBuilder.maker()
            .but(with(ClientMaker.clientStatus, ClientStatus.AVAILABLE),
                with(ClientMaker.uuid, uuidClient),
                with(ClientMaker.coach, null as Coach?))
            .make()

        val client = slot<Client>()

        every { clientRepositoryMock.findByUuid(uuidClient) } returns client0

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

        then(jsonResponse.get("coach").isJsonNull).isTrue
        then(jsonResponse.getAsJsonPrimitive("client").asString).isEqualTo(client.captured.uuid.toString())
        then(jsonResponse.getAsJsonPrimitive("status").asString).isEqualTo("ACCEPTED")

    }


}
