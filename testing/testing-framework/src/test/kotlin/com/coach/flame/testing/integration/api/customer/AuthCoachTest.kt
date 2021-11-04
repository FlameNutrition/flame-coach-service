package com.coach.flame.testing.integration.api.customer

import com.coach.flame.domain.maker.ClientDtoBuilder
import com.coach.flame.domain.maker.ClientDtoMaker
import com.coach.flame.domain.maker.CoachDtoBuilder
import com.coach.flame.domain.maker.CoachDtoMaker
import com.coach.flame.jpa.entity.Client.Companion.toClient
import com.coach.flame.jpa.entity.Coach.Companion.toCoach
import com.coach.flame.jpa.entity.maker.UserMaker
import com.coach.flame.testing.framework.JsonBuilder
import com.coach.flame.testing.framework.LoadRequest
import com.coach.flame.testing.integration.base.BaseIntegrationTest
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMethod

class AuthCoachTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    @LoadRequest(
        pathOfRequest = "requests/integration/customer/registerNewCustomerCoach.json",
        endpoint = "api/customer/create",
        httpMethod = RequestMethod.POST
    )
    fun `register new coach`() {

        // when
        val response = restTemplate.exchange(request!!, String::class.java)

        // then
        then(response).isNotNull
        then(response.statusCode).isEqualTo(HttpStatus.OK)
        then(response.headers.contentType).isEqualTo(MediaType.APPLICATION_JSON)

        val body = JsonBuilder.getJsonFromString(response.body!!)

        then(body.getAsJsonPrimitive("firstname").asString).isEqualTo("Nuno")
        then(body.getAsJsonPrimitive("lastname").asString).isEqualTo("Bento")
        then(body.getAsJsonPrimitive("username").asString).isEqualTo("test@gmail.com")
        then(body.getAsJsonPrimitive("token").asString).isNotEmpty
        then(body.getAsJsonPrimitive("expiration").asString).isNotEmpty
        then(body.getAsJsonPrimitive("type").asString).isEqualTo("COACH")
        then(body.getAsJsonPrimitive("identifier").asString).isNotEmpty
    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/integration/customer/newCustomerSession.json",
        endpoint = "/api/customer/newSession",
        httpMethod = RequestMethod.POST
    )
    fun `get new coach session`() {

        val salt = saltTool.generate()
        val password = hashPasswordTool.generate("12345", salt)

        // when
        val coach = CoachDtoBuilder.maker()
            .but(
                with(CoachDtoMaker.firstName, "Miguel"),
                with(CoachDtoMaker.lastName, "Teixeira"),
                with(
                    CoachDtoMaker.loginInfo, userMaker
                        .but(
                            with(UserMaker.email, "test@gmail.com"),
                            with(UserMaker.password, password),
                            with(UserMaker.key, salt)
                        )
                        .make().toDto()
                )
            )
            .make()
            .toCoach()

        coachRepository.saveAndFlush(coach)

        val response = restTemplate.exchange(request!!, String::class.java)

        // then
        then(response).isNotNull
        then(response.statusCode).isEqualTo(HttpStatus.OK)
        then(response.headers.contentType).isEqualTo(MediaType.APPLICATION_JSON)

        val body = JsonBuilder.getJsonFromString(response.body!!)

        then(body.getAsJsonPrimitive("firstname").asString).isEqualTo("Miguel")
        then(body.getAsJsonPrimitive("lastname").asString).isEqualTo("Teixeira")
        then(body.getAsJsonPrimitive("username").asString).isEqualTo("test@gmail.com")
        then(body.getAsJsonPrimitive("token").asString).isNotEmpty
        then(body.getAsJsonPrimitive("expiration").asString).isNotEmpty
        then(body.getAsJsonPrimitive("type").asString).isEqualTo("COACH")
        then(body.getAsJsonPrimitive("identifier").asString).isEqualTo(coach.uuid.toString())

    }

}
