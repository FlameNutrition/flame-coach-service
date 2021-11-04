package com.coach.flame.testing.integration.api.client.measure

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

class AddWeightMeasureTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    private lateinit var client1: Client

    @BeforeEach
    override fun setup() {
        super.setup()

        client1 = clientRepository.saveAndFlush(
            ClientDtoBuilder.makerWithLoginInfo()
                .but(with(ClientDtoMaker.identifier, UUID.fromString("79275cc8-ed8a-4f8a-b790-ff66f74d758a")))
                .make().toClient()
        )
    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/integration/client/measures/addWeightMeasure.json",
        endpoint = "/api/client/measures/weight/add",
        httpMethod = RequestMethod.POST,
        parameters = ["clientIdentifier:79275cc8-ed8a-4f8a-b790-ff66f74d758a"]
    )
    fun `test add weight for client`() {

        // when
        val response = restTemplate.exchange(request!!, String::class.java)

        // then
        then(response).isNotNull
        then(response.statusCode).isEqualTo(HttpStatus.OK)
        then(response.headers.contentType).isEqualTo(MediaType.APPLICATION_JSON)

        val body = JsonBuilder.getJsonFromString(response.body!!)

        then(body.getAsJsonArray("weights")).hasSize(1)

        val weight = body.getAsJsonArray("weights").first().asJsonObject

        then(weight.getAsJsonPrimitive("date").asString).isEqualTo("2021-05-07")
        then(weight.getAsJsonPrimitive("value").asFloat).isEqualTo(70.5f)
    }

}
