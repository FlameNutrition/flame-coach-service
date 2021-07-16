package com.coach.flame.testing.integration.api.dailyTask

import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.Coach
import com.coach.flame.jpa.entity.DailyTask
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
import java.time.LocalDate
import java.util.*

class DailyTaskDeleteTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    private val client0UUID = UUID.fromString("798cf556-7c23-4637-9aef-a862dc62cba8")
    private val client1UUID = UUID.fromString("3c5845f1-4a90-4396-8610-726176136920")

    private val dailyTask0UUID = UUID.fromString("acfb18c2-be83-4298-bb95-6eeb5e6d39ab")
    private val dailyTask1UUID = UUID.fromString("55af3fb7-6b7a-4baa-b3b7-2e3ad9fcc476")

    private lateinit var client0: Client
    private lateinit var client1: Client

    private lateinit var coach: Coach

    private lateinit var dailyTask0: DailyTask
    private lateinit var dailyTask1: DailyTask
    private lateinit var dailyTask2: DailyTask
    private lateinit var dailyTask3: DailyTask

    @BeforeEach
    override fun setup() {
        super.setup()

        coach = coachRepository.saveAndFlush(CoachBuilder.maker()
            .but(with(CoachMaker.clientType, coachType))
            .make())

        client0 = clientRepository.saveAndFlush(ClientBuilder.maker()
            .but(with(ClientMaker.clientType, clientType),
                with(ClientMaker.uuid, client0UUID))
            .make())

        client1 = clientRepository.saveAndFlush(ClientBuilder.maker()
            .but(with(ClientMaker.clientType, clientType),
                with(ClientMaker.uuid, client1UUID))
            .make())

        dailyTask0 = dailyTaskRepository.saveAndFlush(DailyTaskBuilder.maker()
            .but(with(DailyTaskMaker.createdBy, coach),
                with(DailyTaskMaker.uuid, dailyTask0UUID),
                with(DailyTaskMaker.date, LocalDate.parse("2021-03-21")),
                with(DailyTaskMaker.client, client0))
            .make())
        dailyTask1 = dailyTaskRepository.saveAndFlush(DailyTaskBuilder.maker()
            .but(with(DailyTaskMaker.createdBy, coach),
                with(DailyTaskMaker.uuid, dailyTask1UUID),
                with(DailyTaskMaker.date, LocalDate.parse("2021-03-22")),
                with(DailyTaskMaker.client, client0))
            .make())
        dailyTask2 = dailyTaskRepository.saveAndFlush(DailyTaskBuilder.maker()
            .but(with(DailyTaskMaker.createdBy, coach),
                with(DailyTaskMaker.client, client1))
            .make())
        dailyTask3 = dailyTaskRepository.saveAndFlush(DailyTaskBuilder.maker()
            .but(with(DailyTaskMaker.createdBy, coach),
                with(DailyTaskMaker.date, LocalDate.parse("2021-03-17")),
                with(DailyTaskMaker.client, client0))
            .make())


    }

    @Test
    @LoadRequest(
        endpoint = "/api/dailyTask/delete/task",
        httpMethod = RequestMethod.DELETE,
        headers = [
            "taskUUID:acfb18c2-be83-4298-bb95-6eeb5e6d39ab",
        ]
    )
    fun `test delete daily task`() {

        // when
        val response = restTemplate.exchange(request!!, String::class.java)

        // then
        then(response).isNotNull
        then(response.statusCode).isEqualTo(HttpStatus.OK)
        then(response.headers.contentType).isEqualTo(MediaType.APPLICATION_JSON)

        val jsonResponse = JsonBuilder.getJsonFromString(response.body!!)
        val dailyTasks = jsonResponse.getAsJsonArray("dailyTasks")

        then(dailyTasks).hasSize(1)
        then(dailyTasks.firstOrNull { it.asJsonObject.getAsJsonPrimitive("identifier").asString == dailyTask0.uuid.toString() }).isNotNull

        val dailyTasksUser =
            dailyTaskRepository.findAllByClient(UUID.fromString("798cf556-7c23-4637-9aef-a862dc62cba8"))

        then(dailyTasksUser.get()).hasSize(2)

    }

}
