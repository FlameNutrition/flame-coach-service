package com.coach.flame.jpa.entity.repository

import com.coach.flame.jpa.entity.*
import com.coach.flame.jpa.repository.ClientRepository
import com.coach.flame.jpa.repository.ClientTypeRepository
import com.coach.flame.jpa.repository.UserSessionRepository
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.hibernate.Hibernate
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.ApplicationContext
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

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
        val client = getClientRepository().saveAndFlush(clientMaker
            .but(with(ClientMaker.country, null as CountryConfig?),
                with(ClientMaker.gender, null as GenderConfig?),
                with(ClientMaker.clientType, getClientTypeRepository().getByType("CLIENT")),
                with(ClientMaker.user, userMaker.make()),
                with(ClientMaker.userSession, userSessionMaker.make())
            ).make())
        // COACH
        val coach = getClientRepository().saveAndFlush(clientMaker
            .but(with(ClientMaker.country, null as CountryConfig?),
                with(ClientMaker.gender, null as GenderConfig?),
                with(ClientMaker.clientType, getClientTypeRepository().getByType("COACH")),
                with(ClientMaker.user, userMaker.make()),
                with(ClientMaker.userSession, userSessionMaker.make())
            ).make())

        entityManager.flush()
        entityManager.clear()

        val dailyTask = getDailyTaskRepository().saveAndFlush(dailyTaskMaker
            .but(with(DailyTaskMaker.client, getClientRepository().findByUuid(client.uuid)),
                with(DailyTaskMaker.createdBy, getClientRepository().findByUuid(coach.uuid)))
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