package com.coach.flame.testing.integration.client

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
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.sql.DataSource

class AuthClientIntegrationTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    @LoadRequest(
        pathOfRequest = "requests/integration/client/registerNewClient.json",
        endpoint = "/api/client/create",
        httpMethod = RequestMethod.POST
    )
    fun `register new client`() {

        // when
        clientTypeRepository.saveAndFlush(clientTypeMaker.make())
        val response = restTemplate.restTemplate.exchange(request!!, String::class.java)

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
    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/integration/client/newClientSession.json",
        endpoint = "/api/client/newSession",
        httpMethod = RequestMethod.POST
    )
    fun `get new client session`() {

        // when
        clientTypeRepository.saveAndFlush(clientTypeMaker.make())

        val client = clientMaker
            .but(with(ClientMaker.firstname, "Miguel"))
            .but(with(ClientMaker.lastname, "Teixeira"))
            .but(with(ClientMaker.clientType, clientTypeRepository.getByType("CLIENT")))
            .but(with(ClientMaker.user, userMaker
                .but(with(UserMaker.email, "test@gmail.com"))
                .but(with(UserMaker.password, "12345"))
                .make()))
            .but(with(ClientMaker.country, null as CountryConfig?))
            .but(with(ClientMaker.gender, null as GenderConfig?))
            .but(with(ClientMaker.userSession, userSessionMaker
                .make()))
            .make()

        clientRepository.saveAndFlush(client)

        val response = restTemplate.restTemplate.exchange(request!!, String::class.java)

        println(response)

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

    }

}