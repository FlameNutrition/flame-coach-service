package com.coach.flame.testing.integration.coach

import com.coach.flame.jpa.entity.*
import com.coach.flame.jpa.repository.ClientRepository
import com.coach.flame.jpa.repository.ClientTypeRepository
import com.coach.flame.jpa.repository.UserRepository
import com.coach.flame.testing.framework.JsonBuilder
import com.coach.flame.testing.framework.LoadRequest
import com.coach.flame.testing.integration.base.BaseIntegrationTest
import com.natpryce.makeiteasy.MakeItEasy.*
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestMethod
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.sql.DataSource

class GetClientsCoachIntegrationTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    private lateinit var client1: Client
    private lateinit var client2: Client
    private lateinit var client3: Client
    private lateinit var coach: Coach

    @BeforeEach
    fun setup() {
        clientTypeRepository.saveAndFlush(clientTypeMaker
            .but(with(ClientTypeMaker.type, "CLIENT"))
            .make())
        clientTypeRepository.saveAndFlush(clientTypeMaker
            .but(with(ClientTypeMaker.type, "COACH"))
            .make())

        client1 = clientRepository.saveAndFlush(clientMaker
            .but(with(ClientMaker.uuid, UUID.randomUUID()),
                with(ClientMaker.clientType, clientTypeRepository.getByType("CLIENT")))
            .make())

        client2 = clientRepository.saveAndFlush(clientMaker
            .but(with(ClientMaker.uuid, UUID.randomUUID()),
                with(ClientMaker.clientType, clientTypeRepository.getByType("CLIENT")))
            .make())

        client3 = clientRepository.saveAndFlush(clientMaker
            .but(with(ClientMaker.uuid, UUID.randomUUID()),
                with(ClientMaker.clientType, clientTypeRepository.getByType("CLIENT")))
            .make())

        coach = coachRepository.saveAndFlush(coachMaker
            .but(with(CoachMaker.uuid, UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")),
                with(CoachMaker.clientType, clientTypeRepository.getByType("COACH")),
                with(CoachMaker.clients, listOf(client1, client2, client3)),
                with(CoachMaker.user, userMaker
                    .but(with(UserMaker.userSession, userSessionMaker
                        .but(with(UserSessionMaker.token, UUID.randomUUID()))
                        .make()))
                    .make()))
            .make())

    }

    @Test
    @LoadRequest(
        endpoint = "/api/coach/getClientsAccepted",
        httpMethod = RequestMethod.GET,
        parameters = ["identifier:e59343bc-6563-4488-a77e-112e886c57ae"]
    )
    fun `test get clients from coach`() {

        // when
        val response = restTemplate.exchange(request!!, String::class.java)

        // then
        then(response).isNotNull
        then(response.statusCode).isEqualTo(HttpStatus.OK)
        then(response.headers.contentType).isEqualTo(MediaType.APPLICATION_JSON)

        val body = JsonBuilder.getJsonFromString(response.body!!)

        then(body.getAsJsonPrimitive("identifier").asString).isEqualTo("e59343bc-6563-4488-a77e-112e886c57ae")
        then(body.getAsJsonArray("clientsCoach")).hasSize(3)

        val listOfClients = body.getAsJsonArray("clientsCoach")
            .map { it.asJsonObject.getAsJsonPrimitive("identifier").asString }
            .toList()

        then(listOfClients).contains(client1.uuid.toString())
        then(listOfClients).contains(client2.uuid.toString())
        then(listOfClients).contains(client3.uuid.toString())
    }

}