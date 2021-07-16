package com.coach.flame.testing.integration.api.customer

import com.coach.flame.jpa.entity.CountryConfig
import com.coach.flame.jpa.entity.GenderConfig
import com.coach.flame.jpa.entity.maker.*
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
import java.util.*

class AuthClientTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    @LoadRequest(
        pathOfRequest = "requests/integration/customer/registerNewCustomerClient.json",
        endpoint = "api/customer/create",
        httpMethod = RequestMethod.POST
    )
    fun `register new client`() {

        // when
        val coach = coachRepository.saveAndFlush(CoachBuilder.maker().but(with(CoachMaker.uuid,
            UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")),
            with(CoachMaker.clientType, coachType),
            with(CoachMaker.user, userMaker
                .but(with(UserMaker.userSession, userSessionMaker
                    .but(with(UserSessionMaker.token, UUID.randomUUID()))
                    .make()))
                .make()))
            .make())

        registrationInviteRepository.saveAndFlush(RegistrationInviteBuilder.maker()
            .but(with(RegistrationInviteMaker.coachProp, coach),
                with(RegistrationInviteMaker.registrationKeyProp, "OTk5OS0wMS0wMVQxMjowMDowMF90ZXN0QGdtYWlsLmNvbQ=="))
            .make())


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
        then(body.getAsJsonPrimitive("type").asString).isEqualTo("CLIENT")
        then(body.getAsJsonPrimitive("identifier").asString).isNotEmpty
    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/integration/customer/newCustomerSession.json",
        endpoint = "/api/customer/newSession",
        httpMethod = RequestMethod.POST
    )
    fun `get new client session`() {

        // when
        val salt = saltTool.generate()
        val password = hashPasswordTool.generate("12345", salt)

        val client = clientMaker
            .but(with(ClientMaker.firstname, "Miguel"),
                with(ClientMaker.lastname, "Teixeira"),
                with(ClientMaker.clientType, clientTypeRepository.getByType("CLIENT")),
                with(ClientMaker.user, userMaker
                    .but(with(UserMaker.email, "test@gmail.com"),
                        with(UserMaker.password, password),
                        with(UserMaker.key, salt))
                    .make()),
                with(ClientMaker.country, null as CountryConfig?),
                with(ClientMaker.gender, null as GenderConfig?))
            .make()

        clientRepository.saveAndFlush(client)

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
        then(body.getAsJsonPrimitive("type").asString).isEqualTo("CLIENT")
        then(body.getAsJsonPrimitive("identifier").asString).isEqualTo(client.uuid.toString())

    }

}
