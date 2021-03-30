package com.coach.flame.testing.integration.client

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

class UpdateContactInformationClientTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    private lateinit var client0: Client

    @BeforeEach
    fun setup() {

        val clientType = clientTypeRepository
            .saveAndFlush(ClientTypeBuilder.maker().but(with(ClientTypeMaker.type, "CLIENT")).make())

        countryConfigRepository.saveAndFlush(
            CountryBuilder.maker()
                .but(with(CountryMaker.countryCode, "BR"),
                    with(CountryMaker.externalValue, "Brazil"))
                .make()
        )

        client0 = clientRepository.saveAndFlush(ClientBuilder.maker()
            .but(with(ClientMaker.uuid, UUID.fromString("34cbaa17-0da9-4469-82ec-b1b2ceba9665")),
                with(ClientMaker.weight, 50.4f),
                with(ClientMaker.clientStatus, ClientStatus.ACCEPTED),
                with(ClientMaker.clientType, clientType))
            .make())
    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/integration/client/updateContactInfoClient.json",
        endpoint = "/api/client/updateContactInformation",
        httpMethod = RequestMethod.POST,
        headers = ["clientIdentifier:34cbaa17-0da9-4469-82ec-b1b2ceba9665"]
    )
    fun `update contact information for client`() {

        // when
        val response = restTemplate.exchange(request!!, String::class.java)

        // then
        then(response).isNotNull
        then(response.statusCode).isEqualTo(HttpStatus.OK)
        then(response.headers.contentType).isEqualTo(MediaType.APPLICATION_JSON)

        val body = JsonBuilder.getJsonFromString(response.body!!)

        then(body.getAsJsonPrimitive("identifier").asString).isEqualTo("34cbaa17-0da9-4469-82ec-b1b2ceba9665")
        then(body.getAsJsonPrimitive("firstName").asString).isEqualTo("Nuno")
        then(body.getAsJsonPrimitive("lastName").asString).isEqualTo("Bento")
        then(body.getAsJsonPrimitive("phoneCode").asString).isEqualTo("+44")
        then(body.getAsJsonPrimitive("phoneNumber").asString).isEqualTo("2244556677")
        then(body.getAsJsonObject("country").get("code").asString).isEqualTo("BR")
        then(body.getAsJsonObject("country").get("value").asString).isEqualTo("Brazil")

        val databaseClient = clientRepository.findByUuid(UUID.fromString("34cbaa17-0da9-4469-82ec-b1b2ceba9665"))
        then(databaseClient?.firstName).isEqualTo("Nuno")
        then(databaseClient?.country?.countryCode).isEqualTo("BR")
        then(databaseClient?.weight).isEqualTo(50.4f)
        then(databaseClient?.clientStatus).isEqualTo(ClientStatus.ACCEPTED)

    }

}
