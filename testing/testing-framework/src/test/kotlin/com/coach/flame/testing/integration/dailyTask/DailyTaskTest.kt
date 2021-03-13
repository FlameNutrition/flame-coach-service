package com.coach.flame.testing.integration.dailyTask

import com.coach.flame.jpa.entity.*
import com.coach.flame.testing.framework.JsonBuilder
import com.coach.flame.testing.framework.LoadRequest
import com.coach.flame.testing.integration.base.BaseIntegrationTest
import com.natpryce.makeiteasy.MakeItEasy
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

class DailyTaskTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    private val clientUUID = UUID.fromString("3c5845f1-4a90-4396-8610-7261761369ae")
    private val coachUUID = UUID.randomUUID()
    private val coachUUIDSession = UUID.fromString("b2957c86-e493-4f9a-a277-2e24b77f0ffe")

    @BeforeEach
    fun setup() {
        clientTypeRepository.saveAndFlush(clientTypeMaker
            .but(with(ClientTypeMaker.type, "CLIENT"))
            .make())
        clientTypeRepository.saveAndFlush(clientTypeMaker
            .but(with(ClientTypeMaker.type, "COACH"))
            .make())
    }

    @Test
    @LoadRequest(
        pathOfRequest = "requests/integration/dailyTask/createNewDailyTask.json",
        endpoint = "api/dailyTask/create/task",
        httpMethod = RequestMethod.POST,
        headers = [
            "clientToken:3c5845f1-4a90-4396-8610-7261761369ae",
            "coachToken:b2957c86-e493-4f9a-a277-2e24b77f0ffe"
        ]
    )
    fun `test create new daily task`() {

        // given
        // Client
        clientRepository.saveAndFlush(clientMaker
            .but(with(ClientMaker.uuid, clientUUID),
                with(ClientMaker.clientType, clientTypeRepository.getByType("CLIENT")),
                with(ClientMaker.country, null as CountryConfig?),
                with(ClientMaker.gender, null as GenderConfig?))
            .make())

        // Coach
        clientRepository.saveAndFlush(clientMaker
            .but(with(ClientMaker.uuid, coachUUID),
                with(ClientMaker.clientType, clientTypeRepository.getByType("COACH")),
                with(ClientMaker.country, null as CountryConfig?),
                with(ClientMaker.gender, null as GenderConfig?),
                with(ClientMaker.user, userMaker
                    .but(with(UserMaker.userSession, userSessionMaker
                        .but(with(UserSessionMaker.token, coachUUIDSession))
                        .make()))
                    .make()))
            .make())

        // when
        val response = restTemplate.exchange(request!!, String::class.java)

        // then
        then(response).isNotNull
        then(response.statusCode).isEqualTo(HttpStatus.OK)
        then(response.headers.contentType).isEqualTo(MediaType.APPLICATION_JSON)

        val jsonResponse = JsonBuilder.getJsonFromString(response.body!!)
        val dailyTask0 = jsonResponse.getAsJsonArray("dailyTasks").get(0).asJsonObject

        then(dailyTask0.getAsJsonPrimitive("identifier").asString).isNotEmpty
        then(dailyTask0.getAsJsonPrimitive("taskName").asString).isEqualTo("Drink Water")
        then(dailyTask0.getAsJsonPrimitive("taskDescription").asString).isEqualTo("Drink a 1L of water")
        then(dailyTask0.getAsJsonPrimitive("date").asString).isEqualTo("2020-12-05")
        then(dailyTask0.getAsJsonPrimitive("ticked").asBoolean).isFalse
    }

}