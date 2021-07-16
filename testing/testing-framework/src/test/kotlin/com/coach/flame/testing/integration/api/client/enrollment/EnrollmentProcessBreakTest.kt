package com.coach.flame.testing.integration.api.client.enrollment

import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.ClientStatus
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

class EnrollmentProcessBreakTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    private lateinit var client1: Client
    private lateinit var client2: Client
    private lateinit var client3: Client

    private lateinit var coach1: Coach

    private var isPopulated: Boolean = false

    @BeforeEach
    override fun setup() {

        if (!isPopulated) {
            super.setup()

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
                .but(with(ClientMaker.uuid, UUID.fromString("798cf556-7c23-4637-9aef-a862dc62cba8")),
                    with(ClientMaker.coach, coach1),
                    with(ClientMaker.clientStatus, ClientStatus.ACCEPTED),
                    with(ClientMaker.clientType, clientType))
                .make())

            client2 = clientRepository.saveAndFlush(ClientBuilder.maker()
                .but(with(ClientMaker.uuid, UUID.randomUUID()),
                    with(ClientMaker.coach, coach1),
                    with(ClientMaker.clientStatus, ClientStatus.PENDING),
                    with(ClientMaker.clientType, clientType))
                .make())

            client3 = clientRepository.saveAndFlush(ClientBuilder.maker()
                .but(with(ClientMaker.uuid, UUID.randomUUID()),
                    with(ClientMaker.coach, coach1),
                    with(ClientMaker.clientStatus, ClientStatus.ACCEPTED),
                    with(ClientMaker.clientType, clientType))
                .make())

            isPopulated = true
        }
    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/integration/client/breakEnrollment.json",
        endpoint = "/api/client/enrollment/break",
        httpMethod = RequestMethod.POST,
    )
    fun `break enrollment between client and coach`() {

        // when
        val response = restTemplate.exchange(request!!, String::class.java)

        // then
        then(response).isNotNull
        then(response.statusCode).isEqualTo(HttpStatus.OK)
        then(response.headers.contentType).isEqualTo(MediaType.APPLICATION_JSON)

        val body = JsonBuilder.getJsonFromString(response.body!!)

        then(body.get("coach").isJsonNull).isTrue
        then(body.getAsJsonPrimitive("client").asString).isEqualTo(client1.uuid.toString())
        then(body.getAsJsonPrimitive("status").asString).isEqualTo("AVAILABLE")

        val clientEntity1 = clientRepository.findByUuid(client1.uuid)
        val clientEntity2 = clientRepository.findByUuid(client2.uuid)
        val clientEntity3 = clientRepository.findByUuid(client3.uuid)

        then(clientEntity1!!.coach).isNull()
        then(clientEntity1.clientStatus).isEqualTo(ClientStatus.AVAILABLE)
        then(clientEntity2!!.coach).isNotNull
        then(clientEntity2.clientStatus).isEqualTo(ClientStatus.PENDING)
        then(clientEntity3!!.coach).isNotNull
        then(clientEntity3.clientStatus).isEqualTo(ClientStatus.ACCEPTED)
    }

}
