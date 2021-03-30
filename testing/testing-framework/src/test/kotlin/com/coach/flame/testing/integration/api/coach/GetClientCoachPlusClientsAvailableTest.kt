package com.coach.flame.testing.integration.api.coach

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

class GetClientCoachPlusClientsAvailableTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    private lateinit var client1: Client
    private lateinit var client2: Client
    private lateinit var client3: Client
    private lateinit var client4: Client
    private lateinit var client5: Client
    private lateinit var client6: Client
    private lateinit var coach1: Coach
    private lateinit var coach2: Coach

    @BeforeEach
    fun setup() {
        val clientType = clientTypeRepository
            .saveAndFlush(clientTypeMaker.but(with(ClientTypeMaker.type, "CLIENT")).make())
        val coachType = clientTypeRepository
            .saveAndFlush(clientTypeMaker.but(with(ClientTypeMaker.type, "COACH")).make())

        coach1 = coachRepository.saveAndFlush(coachMaker.but(with(CoachMaker.uuid,
            UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")),
            with(CoachMaker.clientType, coachType),
            with(CoachMaker.user, userMaker
                .but(with(UserMaker.userSession, userSessionMaker
                    .but(with(UserSessionMaker.token, UUID.randomUUID()))
                    .make()))
                .make()))
            .make())

        coach2 = coachRepository.saveAndFlush(coachMaker.but(with(CoachMaker.clientType, coachType))
            .make())

        client1 = clientRepository.saveAndFlush(clientMaker.but(with(ClientMaker.uuid, UUID.randomUUID()),
            with(ClientMaker.clientType, clientType))
            .make())

        client2 = clientRepository.saveAndFlush(clientMaker.but(with(ClientMaker.uuid, UUID.randomUUID()),
            with(ClientMaker.clientType, clientType))
            .make())

        client3 = clientRepository.saveAndFlush(clientMaker.but(with(ClientMaker.uuid, UUID.randomUUID()),
            with(ClientMaker.clientType, clientType))
            .make())

        client4 = clientRepository.saveAndFlush(clientMaker.but(with(ClientMaker.uuid, UUID.randomUUID()),
            with(ClientMaker.clientType, clientType))
            .make())

        client5 = clientRepository.saveAndFlush(clientMaker.but(with(ClientMaker.uuid, UUID.randomUUID()),
            with(ClientMaker.clientType, clientType))
            .make())

        client6 = clientRepository.saveAndFlush(clientMaker.but(with(ClientMaker.uuid, UUID.randomUUID()),
            with(ClientMaker.clientType, clientType),
            with(ClientMaker.clientStatus, ClientStatus.ACCEPTED),
            with(ClientMaker.coach, coach2)).make())
    }

    @Test
    @LoadRequest(
        endpoint = "/api/coach/getClientsCoachPlusClientsAvailable",
        httpMethod = RequestMethod.GET,
        parameters = ["identifier:e59343bc-6563-4488-a77e-112e886c57ae"]
    )
    fun `test get clients from coach plus the clients available for coaching`() {

        // given
        client1.clientStatus = ClientStatus.ACCEPTED
        client1.coach = coach1

        client2.clientStatus = ClientStatus.PENDING
        client2.coach = coach1

        client3.clientStatus = ClientStatus.PENDING
        client3.coach = coach1

        clientRepository.saveAll(listOf(client1, client2, client3))
        clientRepository.flush()

        // when
        val response = restTemplate.exchange(request!!, String::class.java)

        // then
        then(response).isNotNull
        then(response.statusCode).isEqualTo(HttpStatus.OK)
        then(response.headers.contentType).isEqualTo(MediaType.APPLICATION_JSON)

        val body = JsonBuilder.getJsonFromString(response.body!!)

        then(body.getAsJsonPrimitive("identifier").asString).isEqualTo("e59343bc-6563-4488-a77e-112e886c57ae")
        then(body.getAsJsonArray("clientsCoach")).hasSize(5)

        val listOfClients = body.getAsJsonArray("clientsCoach")
            .map { it.asJsonObject.getAsJsonPrimitive("identifier").asString }
            .toList()

        then(listOfClients).contains(client1.uuid.toString())
        then(listOfClients).contains(client2.uuid.toString())
        then(listOfClients).contains(client3.uuid.toString())
        then(listOfClients).contains(client4.uuid.toString())
        then(listOfClients).contains(client5.uuid.toString())
        then(listOfClients).doesNotContain(client6.uuid.toString())
    }

}
