package com.coach.flame.jpa.repository.criteria

import com.coach.flame.jpa.AbstractHelperTest
import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.Coach
import com.coach.flame.jpa.entity.maker.*
import com.coach.flame.jpa.repository.criteria.DailyTaskCriteria.dailyTaskBetweenDate
import com.coach.flame.jpa.repository.criteria.DailyTaskCriteria.dailyTaskClient
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDate

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DailyTaskRepositoryCriteriaTest : AbstractHelperTest() {

    private lateinit var client0: Client
    private lateinit var client1: Client
    private lateinit var client2: Client
    private lateinit var coach0: Coach

    @BeforeEach
    override fun setUp() {
        //FIXME: This should be improved...maybe tests should use @Rules
        super.setUp()
        client0 = ClientBuilder.maker()
            .but(
                with(ClientMaker.clientType, clientType),
                with(ClientMaker.user, UserBuilder.default())
            ).make()

        client1 = ClientBuilder.maker()
            .but(
                with(ClientMaker.clientType, clientType),
                with(ClientMaker.user, UserBuilder.default())
            ).make()

        client2 = ClientBuilder.maker()
            .but(
                with(ClientMaker.clientType, clientType),
                with(ClientMaker.user, UserBuilder.default())
            ).make()

        coach0 = CoachBuilder.maker()
            .but(
                with(CoachMaker.clientType, coachType),
                with(CoachMaker.user, UserBuilder.default()),
                with(CoachMaker.userSession, UserSessionBuilder.default())
            ).make()

        getClientRepository().saveAll(listOf(client0, client1, client2))
        getCoachRepository().saveAndFlush(coach0)

        entityManager.flush()
        entityManager.clear()
    }

    @Test
    fun `test get daily tasks from client using Criteria-dailyTaskClient`() {

        val dailyTask0 = DailyTaskBuilder.maker()
            .but(with(DailyTaskMaker.client, client0),
                with(DailyTaskMaker.createdBy, coach0)
            ).make()
        val dailyTask1 = DailyTaskBuilder.maker()
            .but(with(DailyTaskMaker.client, client1),
                with(DailyTaskMaker.createdBy, coach0)
            ).make()
        val dailyTask2 = DailyTaskBuilder.maker()
            .but(with(DailyTaskMaker.client, client1),
                with(DailyTaskMaker.createdBy, coach0)
            ).make()
        val dailyTask3 = DailyTaskBuilder.maker()
            .but(with(DailyTaskMaker.client, client2),
                with(DailyTaskMaker.createdBy, coach0)
            ).make()
        getDailyTaskRepository().saveAll(listOf(dailyTask0, dailyTask1, dailyTask2, dailyTask3))

        entityManager.flush()
        entityManager.clear()

        val dailyTasksClient0 = getDailyTaskRepository().findAll(dailyTaskClient(client0.uuid))
        val dailyTasksClient1 = getDailyTaskRepository().findAll(dailyTaskClient(client1.uuid))

        then(dailyTasksClient0).isNotEmpty
        then(dailyTasksClient1).isNotEmpty

        then(dailyTasksClient0.first()).isEqualTo(dailyTask0)
        then(dailyTasksClient1).hasSize(2)

    }

    @Test
    fun `test get daily tasks using Criteria-dailyTaskDay`() {

        val actualDate = LocalDate.now()
        val dailyTask0 = DailyTaskBuilder.maker()
            .but(with(DailyTaskMaker.client, client0),
                with(DailyTaskMaker.createdBy, coach0),
                with(DailyTaskMaker.date, actualDate.plusDays(1))
            ).make()
        val dailyTask1 = DailyTaskBuilder.maker()
            .but(with(DailyTaskMaker.client, client1),
                with(DailyTaskMaker.createdBy, coach0),
                with(DailyTaskMaker.date, actualDate.minusDays(1))
            ).make()
        val dailyTask2 = DailyTaskBuilder.maker()
            .but(with(DailyTaskMaker.client, client1),
                with(DailyTaskMaker.createdBy, coach0),
                with(DailyTaskMaker.date, actualDate.minusMonths(1))
            ).make()
        getDailyTaskRepository().saveAll(listOf(dailyTask0, dailyTask1, dailyTask2))

        entityManager.flush()
        entityManager.clear()

        val dailyTasks = getDailyTaskRepository()
            .findAll(dailyTaskBetweenDate(actualDate.minusDays(1), actualDate.plusDays(1)))

        then(dailyTasks).isNotEmpty
        then(dailyTasks).hasSize(2)

        then(dailyTasks.first { task -> task.client.uuid == client0.uuid }).isEqualTo(dailyTask0)
        then(dailyTasks.first { task -> task.client.uuid == client1.uuid }).isEqualTo(dailyTask1)

    }

    @Test
    fun `test get daily tasks from client using combined Criteria`() {

        val actualDate = LocalDate.now()
        val dailyTask0 = DailyTaskBuilder.maker()
            .but(with(DailyTaskMaker.client, client0),
                with(DailyTaskMaker.createdBy, coach0),
                with(DailyTaskMaker.date, actualDate.plusDays(1))
            ).make()
        val dailyTask1 = DailyTaskBuilder.maker()
            .but(with(DailyTaskMaker.client, client1),
                with(DailyTaskMaker.createdBy, coach0),
                with(DailyTaskMaker.date, actualDate.minusDays(1))
            ).make()
        val dailyTask2 = DailyTaskBuilder.maker()
            .but(with(DailyTaskMaker.client, client1),
                with(DailyTaskMaker.createdBy, coach0),
                with(DailyTaskMaker.date, actualDate.minusMonths(1))
            ).make()
        getDailyTaskRepository().saveAll(listOf(dailyTask0, dailyTask1, dailyTask2))

        entityManager.flush()
        entityManager.clear()

        val dailyTaskFromClientBetweenDays = dailyTaskClient(client1.uuid)
            .and(dailyTaskBetweenDate(actualDate.minusDays(1), actualDate.plusDays(1)))

        val dailyTasks = getDailyTaskRepository().findAll(dailyTaskFromClientBetweenDays)

        then(dailyTasks).isNotEmpty
        then(dailyTasks).hasSize(1)

        then(dailyTasks.first { task -> task.client.uuid == client1.uuid }).isEqualTo(dailyTask1)

    }

}
