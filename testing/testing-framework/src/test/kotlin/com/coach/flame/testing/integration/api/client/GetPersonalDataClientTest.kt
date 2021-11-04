package com.coach.flame.testing.integration.api.client

import com.coach.flame.domain.maker.ClientDtoBuilder
import com.coach.flame.domain.maker.ClientDtoMaker
import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.Client.Companion.toClient
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

class GetPersonalDataClientTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    private lateinit var client0: Client

    @BeforeEach
    override fun setup() {

        super.setup()

        client0 = clientRepository.saveAndFlush(
            ClientDtoBuilder.makerWithLoginInfo()
                .but(
                    with(ClientDtoMaker.identifier, UUID.fromString("34cbaa17-0da9-4469-82ec-b1b2ceba9665")),
                    with(ClientDtoMaker.weight, 76.5f),
                    with(ClientDtoMaker.height, 176f)
                )
                .make().toClient()
        )
    }

    @Test
    @LoadRequest(
        endpoint = "/api/client/getPersonalData",
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

        then(body.getAsJsonPrimitive("weight").asFloat).isEqualTo(76.5f)
        then(body.getAsJsonPrimitive("height").asFloat).isEqualTo(176f)
        then(body.get("gender").isJsonNull).isTrue
        then(body.getAsJsonObject("measureType").asJsonObject.get("value").asString).isEqualTo("Kg/cm")
        then(body.getAsJsonObject("measureType").asJsonObject.get("code").asString).isEqualTo("KG_CM")

    }

}
