package com.coach.flame.testing.integration.api.dailyTask

import com.coach.flame.jpa.entity.*
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

class DailyTaskCreateTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    private val clientUUID = UUID.fromString("3c5845f1-4a90-4396-8610-7261761369ae")
    private val coachUUID = UUID.fromString("b2957c86-e493-4f9a-a277-2e24b77f0ffe")

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
            "clientIdentifier:3c5845f1-4a90-4396-8610-7261761369ae",
            "coachIdentifier:b2957c86-e493-4f9a-a277-2e24b77f0ffe"
        ]
    )
    fun `test create new daily task`() {

        // given
        // Client
        clientRepository.saveAndFlush(ClientBuilder.maker()
            .but(with(ClientMaker.uuid, clientUUID),
                with(ClientMaker.clientType, clientTypeRepository.getByType("CLIENT")))
            .make())

        // Coach
        coachRepository.saveAndFlush(CoachBuilder.maker()
            .but(with(CoachMaker.uuid, coachUUID),
                with(CoachMaker.clientType, clientTypeRepository.getByType("COACH")))
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
