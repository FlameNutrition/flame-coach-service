package com.coach.flame.testing.integration.api.client.enrollment

import com.coach.flame.jpa.entity.*
import com.coach.flame.testing.framework.JsonBuilder
import com.coach.flame.testing.framework.LoadRequest
import com.coach.flame.testing.integration.base.BaseIntegrationTest
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMethod
import java.util.*

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class EnrollmentProcessAcceptedTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    private lateinit var client1: Client
    private lateinit var client2: Client
    private lateinit var client3: Client

    private lateinit var coach1: Coach

    private var isPopulated: Boolean = false

    @BeforeEach
    fun setup() {

        if (!isPopulated) {

            enableDatabaseClean = false

            val clientType = clientTypeRepository
                .saveAndFlush(ClientTypeBuilder.maker().but(with(ClientTypeMaker.type, "CLIENT")).make())
            val coachType = clientTypeRepository
                .saveAndFlush(ClientTypeBuilder.maker().but(with(ClientTypeMaker.type, "COACH")).make())

            coach1 = coachRepository.saveAndFlush(CoachBuilder.maker().but(with(CoachMaker.uuid,
                UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")),
                with(CoachMaker.clientType, coachType),
                with(CoachMaker.user, userMaker
                    .but(with(UserMaker.userSession, userSessionMaker
                        .but(with(UserSessionMaker.token, UUID.randomUUID()))
                        .make()))
                    .make()))
                .make())

            client1 = clientRepository.saveAndFlush(ClientBuilder.maker()
                .but(with(ClientMaker.uuid, UUID.fromString("34cbaa17-0da9-4469-82ec-b1b2ceba9665")),
                    with(ClientMaker.clientType, clientType))
                .make())

            client2 = clientRepository.saveAndFlush(ClientBuilder.maker().but(with(ClientMaker.uuid, UUID.randomUUID()),
                with(ClientMaker.clientType, clientType))
                .make())

            client3 = clientRepository.saveAndFlush(ClientBuilder.maker().but(with(ClientMaker.uuid, UUID.randomUUID()),
                with(ClientMaker.clientType, clientType))
                .make())

            isPopulated = true
        }
    }

    @Test
    @Order(1)
    @LoadRequest(
        pathOfRequest = "requests/integration/client/initEnrollment.json",
        endpoint = "/api/client/enrollment/init",
        httpMethod = RequestMethod.POST,
    )
    fun `first step - init enrollment process`() {

        // when
        val response = restTemplate.exchange(request!!, String::class.java)

        // then
        then(response).isNotNull
        then(response.statusCode).isEqualTo(HttpStatus.OK)
        then(response.headers.contentType).isEqualTo(MediaType.APPLICATION_JSON)

        val body = JsonBuilder.getJsonFromString(response.body!!)

        then(body.getAsJsonObject("coach").getAsJsonPrimitive("identifier").asString).isEqualTo(coach1.uuid.toString())
        then(body.getAsJsonObject("coach").getAsJsonPrimitive("firstName").asString).isEqualTo(coach1.firstName)
        then(body.getAsJsonObject("coach").getAsJsonPrimitive("lastName").asString).isEqualTo(coach1.lastName)
        then(body.getAsJsonPrimitive("client").asString).isEqualTo(client1.uuid.toString())
        then(body.getAsJsonPrimitive("status").asString).isEqualTo("PENDING")
    }

    @Test
    @Order(2)
    @LoadRequest(
        pathOfRequest = "requests/integration/client/finishEnrollmentAccepted.json",
        endpoint = "/api/client/enrollment/finish",
        httpMethod = RequestMethod.POST,
    )
    fun `second step - finish enrollment process`() {

        // when
        val response = restTemplate.exchange(request!!, String::class.java)

        // then
        then(response).isNotNull
        then(response.statusCode).isEqualTo(HttpStatus.OK)
        then(response.headers.contentType).isEqualTo(MediaType.APPLICATION_JSON)

        val body = JsonBuilder.getJsonFromString(response.body!!)

        then(body.getAsJsonObject("coach").getAsJsonPrimitive("identifier").asString).isEqualTo(coach1.uuid.toString())
        then(body.getAsJsonObject("coach").getAsJsonPrimitive("firstName").asString).isEqualTo(coach1.firstName)
        then(body.getAsJsonObject("coach").getAsJsonPrimitive("lastName").asString).isEqualTo(coach1.lastName)
        then(body.getAsJsonPrimitive("client").asString).isEqualTo(client1.uuid.toString())
        then(body.getAsJsonPrimitive("status").asString).isEqualTo("ACCEPTED")

        enableDatabaseClean = true

    }

}
