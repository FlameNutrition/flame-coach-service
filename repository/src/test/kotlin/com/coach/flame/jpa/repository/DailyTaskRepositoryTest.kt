package com.coach.flame.jpa.repository

import com.coach.flame.jpa.AbstractHelperTest
import com.coach.flame.jpa.entity.CountryConfig
import com.coach.flame.jpa.entity.GenderConfig
import com.coach.flame.jpa.entity.maker.*
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DailyTaskRepositoryTest : AbstractHelperTest() {

    @Test
    fun `test create daily task and check cascade entities`() {

        getClientTypeRepository().saveAndFlush(clientTypeMaker
            .but(with(ClientTypeMaker.type, "CLIENT"))
            .make())
        getClientTypeRepository().saveAndFlush(clientTypeMaker
            .but(with(ClientTypeMaker.type, "COACH"))
            .make())

        // CLIENT
        val client = getClientRepository().saveAndFlush(ClientBuilder.maker()
            .but(with(ClientMaker.country, null as CountryConfig?),
                with(ClientMaker.gender, null as GenderConfig?),
                with(ClientMaker.clientType, getClientTypeRepository().getByType("CLIENT")),
                with(ClientMaker.user, userMaker.make())
            ).make())
        // COACH
        val coach = getCoachRepository().saveAndFlush(CoachBuilder.maker()
            .but(with(CoachMaker.clientType, getClientTypeRepository().getByType("COACH")),
                with(CoachMaker.user, userMaker.make()),
                with(CoachMaker.userSession, userSessionMaker.make())
            ).make())

        entityManager.flush()
        entityManager.clear()

        val dailyTask = getDailyTaskRepository().saveAndFlush(dailyTaskMaker
            .but(with(DailyTaskMaker.client, client), with(DailyTaskMaker.createdBy, coach))
            .make())

        entityManager.flush()
        entityManager.clear()

        then(dailyTask.id).isNotNull

        val dailyTaskBidirectional = getDailyTaskRepository().findById(dailyTask.id!!)

        // Test bidirectional relationship
        then(dailyTaskBidirectional.get().client).isNotNull
        then(dailyTaskBidirectional.get().createdBy).isNotNull

    }

}
