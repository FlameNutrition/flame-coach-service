package com.coach.flame.testing.integration.client

import com.coach.flame.testing.framework.JsonBuilder
import com.coach.flame.testing.integration.base.BaseIntegrationTest
import com.coach.flame.testing.framework.LoadRequest
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestMethod

class AuthClientIntegrationTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    @Transactional
    @LoadRequest(
        pathOfRequest = "requests/integration/client/registerNewClient.json",
        endpoint = "/api/client/create",
        httpMethod = RequestMethod.POST
    )
    @Sql("/sql/configs/clientTypeConfig.sql")
    fun `register new client`() {

        // when
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
    }

}