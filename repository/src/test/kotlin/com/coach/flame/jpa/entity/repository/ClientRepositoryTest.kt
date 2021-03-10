package com.coach.flame.jpa.entity.repository

import com.coach.flame.jpa.entity.AbstractHelperTest
import com.coach.flame.jpa.entity.ClientMaker
import com.coach.flame.jpa.entity.CountryConfig
import com.coach.flame.jpa.entity.GenderConfig
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
class ClientRepositoryTest : AbstractHelperTest() {

    @Test
    fun `test create client and check cascade entities`() {

        getClientTypeRepository().saveAndFlush(clientTypeMaker.make())

        val clientToTest = clientMaker
            .but(with(ClientMaker.country, null as CountryConfig?),
                with(ClientMaker.gender, null as GenderConfig?),
                with(ClientMaker.clientType, getClientTypeRepository().getByType("CLIENT")),
                with(ClientMaker.user, userMaker.make()),
                with(ClientMaker.userSession, userSessionMaker.make())
            ).make()

        getClientRepository().saveAndFlush(clientToTest)

        entityManager.flush()
        entityManager.clear()

        val client = getClientRepository().findAll().first()
        val userSession = getUserSessionRepositoryRepository().findAll().first()

        then(client.id).isNotNull
        then(client.user).isNotNull
        then(client.user.userSession).isNotNull

        // Test bidirectional relationship
        then(userSession.user).isNotNull
        then(userSession.user?.client).isNotNull

    }

}