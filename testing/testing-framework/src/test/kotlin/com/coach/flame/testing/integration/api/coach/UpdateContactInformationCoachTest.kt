package com.coach.flame.testing.integration.api.coach

import com.coach.flame.jpa.entity.Coach
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
import java.util.*

class UpdateContactInformationCoachTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    private lateinit var coach0: Coach

    @BeforeEach
    fun setup() {

        val clientType = clientTypeRepository
            .saveAndFlush(ClientTypeBuilder.maker().but(with(ClientTypeMaker.type, "COACH")).make())

        countryConfigRepository.saveAndFlush(
            CountryBuilder.maker()
                .but(with(CountryMaker.countryCode, "BR"),
                    with(CountryMaker.externalValue, "Brazil"))
                .make()
        )

        coach0 = coachRepository.saveAndFlush(CoachBuilder.maker()
            .but(with(CoachMaker.uuid, UUID.fromString("34cbaa17-0da9-4469-82ec-b1b2ceba9665")),
                with(CoachMaker.clientType, clientType))
            .make())
    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/integration/coach/updateContactInfoCoach.json",
        endpoint = "/api/coach/updateContactInformation",
        httpMethod = RequestMethod.POST,
        headers = ["coachIdentifier:34cbaa17-0da9-4469-82ec-b1b2ceba9665"]
    )
    fun `update contact information for coach`() {

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

    }

}
