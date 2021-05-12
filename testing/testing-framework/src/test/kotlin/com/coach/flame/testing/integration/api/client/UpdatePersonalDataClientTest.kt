package com.coach.flame.testing.integration.api.client

import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.ClientStatus
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

class UpdatePersonalDataClientTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    private lateinit var client0: Client

    @BeforeEach
    fun setup() {

        val clientType = clientTypeRepository
            .saveAndFlush(ClientTypeBuilder.maker().but(with(ClientTypeMaker.type, "CLIENT")).make())

        genderConfigRepository.saveAndFlush(
            GenderBuilder.maker()
                .but(with(GenderMaker.genderCode, "F"),
                    with(GenderMaker.externalValue, "Female"))
                .make()
        )

        client0 = clientRepository.saveAndFlush(ClientBuilder.maker()
            .but(with(ClientMaker.uuid, UUID.fromString("34cbaa17-0da9-4469-82ec-b1b2ceba9665")),
                with(ClientMaker.firstname, "Nuno"),
                with(ClientMaker.registrationDate, LocalDate.parse("2021-04-04")),
                with(ClientMaker.clientStatus, ClientStatus.PENDING),
                with(ClientMaker.clientType, clientType))
            .make())
    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/integration/client/updatePersonalDataClient.json",
        endpoint = "/api/client/updatePersonalData",
        httpMethod = RequestMethod.POST,
        headers = ["clientIdentifier:34cbaa17-0da9-4469-82ec-b1b2ceba9665"]
    )
    fun `update personal data for client`() {

        // when
        val response = restTemplate.exchange(request!!, String::class.java)

        // then
        then(response).isNotNull
        then(response.statusCode).isEqualTo(HttpStatus.OK)
        then(response.headers.contentType).isEqualTo(MediaType.APPLICATION_JSON)

        val body = JsonBuilder.getJsonFromString(response.body!!)

        then(body.getAsJsonPrimitive("weight").asFloat).isEqualTo(143.3f)
        then(body.getAsJsonPrimitive("height").asFloat).isEqualTo(167f)
        then(body.getAsJsonObject("gender").asJsonObject.get("value").asString).isEqualTo("Female")
        then(body.getAsJsonObject("gender").asJsonObject.get("code").asString).isEqualTo("F")
        then(body.getAsJsonObject("measureType").asJsonObject.get("value").asString).isEqualTo("Lbs/in")
        then(body.getAsJsonObject("measureType").asJsonObject.get("code").asString).isEqualTo("LBS_IN")

        val databaseClient = clientRepository.findByUuid(UUID.fromString("34cbaa17-0da9-4469-82ec-b1b2ceba9665"))
        then(databaseClient?.firstName).isEqualTo("Nuno")
        then(databaseClient?.registrationDate).isEqualTo(LocalDate.parse("2021-04-04"))
        then(databaseClient?.gender?.genderCode).isEqualTo("F")
        then(databaseClient?.clientStatus).isEqualTo(ClientStatus.PENDING)

    }

}
