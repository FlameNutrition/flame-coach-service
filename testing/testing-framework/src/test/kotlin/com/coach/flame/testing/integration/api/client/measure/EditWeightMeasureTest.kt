package com.coach.flame.testing.integration.api.client.measure

import com.coach.flame.domain.maker.ClientDtoBuilder
import com.coach.flame.domain.maker.ClientDtoMaker
import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.Client.Companion.toClient
import com.coach.flame.jpa.entity.ClientMeasureWeight
import com.coach.flame.jpa.entity.maker.ClientMeasureWeightBuilder
import com.coach.flame.jpa.entity.maker.ClientMeasureWeightMaker
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
import java.time.LocalDate
import java.util.*

class EditWeightMeasureTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    private lateinit var client1: Client
    private lateinit var weight0: ClientMeasureWeight

    @BeforeEach
    override fun setup() {

        super.setup()

        weight0 = ClientMeasureWeightBuilder.maker()
            .but(
                with(ClientMeasureWeightMaker.weight, 70.5f),
                with(ClientMeasureWeightMaker.measureDate, LocalDate.of(2021, 5, 20))
            )
            .make()

        client1 = clientRepository.saveAndFlush(
            ClientDtoBuilder.makerWithLoginInfo()
                .but(
                    with(ClientDtoMaker.identifier, UUID.fromString("79275cc8-ed8a-4f8a-b790-ff66f74d758a")),
                    with(ClientDtoMaker.listOfWeights, mutableListOf(weight0.toDto()))
                )
                .make()
                .toClient()
        )
    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/integration/client/measures/editWeightMeasure.json",
        endpoint = "/api/client/measures/weight/edit",
        httpMethod = RequestMethod.POST,
        parameters = [
            "clientIdentifier:79275cc8-ed8a-4f8a-b790-ff66f74d758a",
            "identifier:1"
        ]
    )
    fun `test edit weight from client`() {

        // when
        val response = restTemplate.exchange(request!!, String::class.java)

        // then
        then(response).isNotNull
        then(response.statusCode).isEqualTo(HttpStatus.OK)
        then(response.headers.contentType).isEqualTo(MediaType.APPLICATION_JSON)

        val body = JsonBuilder.getJsonFromString(response.body!!)

        then(body.getAsJsonArray("weights")).hasSize(1)

        val weight0 = body.getAsJsonArray("weights")
            .first().asJsonObject

        then(weight0?.getAsJsonPrimitive("date")?.asString).isEqualTo("2021-05-01")
        then(weight0?.getAsJsonPrimitive("value")?.asFloat).isEqualTo(80.7f)
    }

}
