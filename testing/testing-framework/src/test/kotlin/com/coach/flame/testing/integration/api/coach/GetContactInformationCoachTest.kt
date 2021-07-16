package com.coach.flame.testing.integration.api.coach

import com.coach.flame.jpa.entity.Coach
import com.coach.flame.jpa.entity.maker.ClientTypeBuilder
import com.coach.flame.jpa.entity.maker.ClientTypeMaker
import com.coach.flame.jpa.entity.maker.CoachBuilder
import com.coach.flame.jpa.entity.maker.CoachMaker
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

class GetContactInformationCoachTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    private lateinit var coach0: Coach

    @BeforeEach
    override fun setup() {
        super.setup()

        coach0 = coachRepository.saveAndFlush(CoachBuilder.maker()
            .but(with(CoachMaker.uuid, UUID.fromString("34cbaa17-0da9-4469-82ec-b1b2ceba9665")),
                with(CoachMaker.phoneCode, "+44"),
                with(CoachMaker.phoneNumber, "22334455"),
                with(CoachMaker.clientType, clientType))
            .make())
    }

    @Test
    @LoadRequest(
        endpoint = "/api/coach/getContactInformation",
        httpMethod = RequestMethod.GET,
        headers = ["coachIdentifier:34cbaa17-0da9-4469-82ec-b1b2ceba9665"]
    )
    fun `get contact information for coach`() {

        // when
        val response = restTemplate.exchange(request!!, String::class.java)

        // then
        then(response).isNotNull
        then(response.statusCode).isEqualTo(HttpStatus.OK)
        then(response.headers.contentType).isEqualTo(MediaType.APPLICATION_JSON)

        val body = JsonBuilder.getJsonFromString(response.body!!)

        then(body.getAsJsonPrimitive("identifier").asString).isEqualTo("34cbaa17-0da9-4469-82ec-b1b2ceba9665")
        then(body.getAsJsonPrimitive("firstName").asString).isEqualTo(coach0.firstName)
        then(body.getAsJsonPrimitive("lastName").asString).isEqualTo(coach0.lastName)
        then(body.getAsJsonPrimitive("phoneCode").asString).isEqualTo(coach0.phoneCode)
        then(body.getAsJsonPrimitive("phoneNumber").asString).isEqualTo(coach0.phoneNumber)
        then(body.get("country").isJsonNull).isTrue

    }

}
