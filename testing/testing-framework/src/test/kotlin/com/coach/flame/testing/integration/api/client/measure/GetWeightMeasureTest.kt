package com.coach.flame.testing.integration.api.client.measure

import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.ClientMeasureWeight
import com.coach.flame.jpa.entity.maker.*
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

class GetWeightMeasureTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    private lateinit var client1: Client
    private lateinit var weight0: ClientMeasureWeight
    private lateinit var weight1: ClientMeasureWeight
    private lateinit var weight2: ClientMeasureWeight

    @BeforeEach
    override fun setup() {

        super.setup()

        weight0 = ClientMeasureWeightBuilder.maker()
            .but(with(ClientMeasureWeightMaker.weight, 70.5f),
                with(ClientMeasureWeightMaker.measureDate, LocalDate.of(2021, 5, 20)))
            .make()
        weight1 = ClientMeasureWeightBuilder.maker()
            .but(with(ClientMeasureWeightMaker.weight, 80.5f),
                with(ClientMeasureWeightMaker.measureDate, LocalDate.of(2021, 6, 20)))
            .make()
        weight2 = ClientMeasureWeightBuilder.maker()
            .but(with(ClientMeasureWeightMaker.weight, 90.5f),
                with(ClientMeasureWeightMaker.measureDate, LocalDate.of(2021, 7, 20)))
            .make()

        client1 = clientRepository.saveAndFlush(ClientBuilder.maker()
            .but(with(ClientMaker.uuid, UUID.fromString("79275cc8-ed8a-4f8a-b790-ff66f74d758a")),
                with(ClientMaker.clientMeasureWeight, mutableListOf(weight0, weight1, weight2)),
                with(ClientMaker.clientType, clientType))
            .make())
    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/integration/client/measures/addWeightMeasure.json",
        endpoint = "/api/client/measures/weight/get",
        httpMethod = RequestMethod.GET,
        parameters = ["clientIdentifier:79275cc8-ed8a-4f8a-b790-ff66f74d758a"]
    )
    fun `test get weight from client`() {

        // when
        val response = restTemplate.exchange(request!!, String::class.java)

        // then
        then(response).isNotNull
        then(response.statusCode).isEqualTo(HttpStatus.OK)
        then(response.headers.contentType).isEqualTo(MediaType.APPLICATION_JSON)

        val body = JsonBuilder.getJsonFromString(response.body!!)

        then(body.getAsJsonArray("weights")).hasSize(3)

        val weight0 = body.getAsJsonArray("weights")
            .find { it.asJsonObject.getAsJsonPrimitive("identifier").asLong == weight0.toDto().id }?.asJsonObject
        val weight1 = body.getAsJsonArray("weights")
            .find { it.asJsonObject.getAsJsonPrimitive("identifier").asLong == weight1.toDto().id }?.asJsonObject
        val weight2 = body.getAsJsonArray("weights")
            .find { it.asJsonObject.getAsJsonPrimitive("identifier").asLong == weight2.toDto().id }?.asJsonObject

        then(weight0?.getAsJsonPrimitive("date")?.asString).isEqualTo("2021-05-20")
        then(weight0?.getAsJsonPrimitive("value")?.asFloat).isEqualTo(70.5f)

        then(weight1?.getAsJsonPrimitive("date")?.asString).isEqualTo("2021-06-20")
        then(weight1?.getAsJsonPrimitive("value")?.asFloat).isEqualTo(80.5f)

        then(weight2?.getAsJsonPrimitive("date")?.asString).isEqualTo("2021-07-20")
        then(weight2?.getAsJsonPrimitive("value")?.asFloat).isEqualTo(90.5f)
    }

}
