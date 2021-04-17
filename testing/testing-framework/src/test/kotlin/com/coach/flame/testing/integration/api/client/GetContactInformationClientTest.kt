package com.coach.flame.testing.integration.api.client

import com.coach.flame.jpa.entity.*
import com.coach.flame.testing.framework.JsonBuilder
import com.coach.flame.testing.framework.LoadRequest
import com.coach.flame.testing.integration.base.BaseIntegrationTest
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMethod
import java.util.*

class GetContactInformationClientTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    private lateinit var client0: Client

    @BeforeEach
    fun setup() {

        val clientType = clientTypeRepository
            .saveAndFlush(ClientTypeBuilder.maker().but(with(ClientTypeMaker.type, "CLIENT")).make())

        client0 = clientRepository.saveAndFlush(ClientBuilder.maker()
            .but(with(ClientMaker.uuid, UUID.fromString("34cbaa17-0da9-4469-82ec-b1b2ceba9665")),
                with(ClientMaker.phoneCode, "+44"),
                with(ClientMaker.phoneNumber, "22334455"),
                with(ClientMaker.clientType, clientType))
            .make())
    }

    @Test
    @LoadRequest(
        endpoint = "/api/client/getContactInformation",
        httpMethod = RequestMethod.GET,
        headers = ["clientIdentifier:34cbaa17-0da9-4469-82ec-b1b2ceba9665"]
    )
    fun `get contact information for client`() {

        // when
        val response = restTemplate.exchange(request!!, String::class.java)

        // then
        then(response).isNotNull
        then(response.statusCode).isEqualTo(HttpStatus.OK)
        then(response.headers.contentType).isEqualTo(MediaType.APPLICATION_JSON)

        val body = JsonBuilder.getJsonFromString(response.body!!)

        then(body.getAsJsonPrimitive("identifier").asString).isEqualTo("34cbaa17-0da9-4469-82ec-b1b2ceba9665")
        then(body.getAsJsonPrimitive("firstName").asString).isEqualTo(client0.firstName)
        then(body.getAsJsonPrimitive("lastName").asString).isEqualTo(client0.lastName)
        then(body.getAsJsonPrimitive("phoneCode").asString).isEqualTo(client0.phoneCode)
        then(body.getAsJsonPrimitive("phoneNumber").asString).isEqualTo(client0.phoneNumber)
        then(body.get("country").isJsonNull).isTrue

    }

}