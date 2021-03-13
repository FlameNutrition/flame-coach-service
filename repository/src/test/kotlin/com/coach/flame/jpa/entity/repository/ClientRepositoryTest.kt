package com.coach.flame.jpa.entity.repository

import com.coach.flame.jpa.entity.*
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

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

    @Test
    fun `test get clients with coach`() {

        val coachType = getClientTypeRepository()
            .saveAndFlush(clientTypeMaker.but(with(ClientTypeMaker.type, "COACH")).make())
        val clientType = getClientTypeRepository()
            .saveAndFlush(clientTypeMaker.but(with(ClientTypeMaker.type, "CLIENT")).make())

        val coach0 = getCoachRepository()
            .saveAndFlush(coachMaker.but(with(CoachMaker.clientType, coachType)).make())
        val coach1 = getCoachRepository()
            .saveAndFlush(coachMaker.but(with(CoachMaker.clientType, coachType)).make())

        val client0 = getClientRepository()
            .saveAndFlush(clientMaker.but(with(ClientMaker.clientType, clientType)).make())
        val client1 = getClientRepository()
            .saveAndFlush(clientMaker.but(with(ClientMaker.clientType, clientType)).make())
        val client2 = getClientRepository()
            .saveAndFlush(clientMaker.but(with(ClientMaker.clientType, clientType)).make())
        val client3 = getClientRepository()
            .saveAndFlush(clientMaker.but(with(ClientMaker.clientType, clientType)).make())

        entityManager.flush()
        entityManager.clear()

        then(getClientRepository().findAll()).hasSize(4)
        then(getCoachRepository().findAll()).hasSize(2)

        client0.coach = coach0
        client0.clientStatus = ClientStatus.PENDING

        client1.coach = coach0
        client1.clientStatus = ClientStatus.ACCEPTED

        client2.coach = coach1
        client2.clientStatus = ClientStatus.ACCEPTED

        getClientRepository().saveAll(listOf(client0, client1, client2))

        entityManager.flush()
        entityManager.clear()

        val listOfClientCoach0 = getClientRepository().findClientsWithCoach(coach0.uuid)
            .map { it.uuid }
        val listOfClientCoach1 = getClientRepository().findClientsWithCoach(coach1.uuid)
            .map { it.uuid }

        then(listOfClientCoach0).contains(client0.uuid)
        then(listOfClientCoach0).contains(client1.uuid)
        then(listOfClientCoach0).doesNotContain(client3.uuid)
        then(listOfClientCoach0).doesNotContain(client2.uuid)
        then(listOfClientCoach0).hasSize(2)

        then(listOfClientCoach1).contains(client2.uuid)
        then(listOfClientCoach1).doesNotContain(client3.uuid)
        then(listOfClientCoach1).doesNotContain(client0.uuid)
        then(listOfClientCoach1).doesNotContain(client1.uuid)
        then(listOfClientCoach1).hasSize(1)

    }

    @Test
    fun `test get clients without coach`() {

        val coachType = getClientTypeRepository()
            .saveAndFlush(clientTypeMaker.but(with(ClientTypeMaker.type, "COACH")).make())
        val clientType = getClientTypeRepository()
            .saveAndFlush(clientTypeMaker.but(with(ClientTypeMaker.type, "CLIENT")).make())

        val coach0 = getCoachRepository()
            .saveAndFlush(coachMaker.but(with(CoachMaker.clientType, coachType)).make())

        val client0 = getClientRepository()
            .saveAndFlush(clientMaker.but(with(ClientMaker.clientType, clientType)).make())
        val client1 = getClientRepository()
            .saveAndFlush(clientMaker.but(with(ClientMaker.clientType, clientType)).make())
        val client2 = getClientRepository()
            .saveAndFlush(clientMaker.but(with(ClientMaker.clientType, clientType)).make())

        entityManager.flush()
        entityManager.clear()

        then(getClientRepository().findAll()).hasSize(3)
        then(getCoachRepository().findAll()).hasSize(1)

        client0.coach = coach0
        client0.clientStatus = ClientStatus.PENDING

        getClientRepository().saveAll(listOf(client0))

        entityManager.flush()
        entityManager.clear()

        val listOfClientsWithoutCoach = getClientRepository().findClientsWithoutCoach()
            .map { it.uuid }

        then(listOfClientsWithoutCoach).contains(client1.uuid)
        then(listOfClientsWithoutCoach).contains(client2.uuid)
        then(listOfClientsWithoutCoach).doesNotContain(client0.uuid)
        then(listOfClientsWithoutCoach).hasSize(2)

    }

}