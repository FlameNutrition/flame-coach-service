package com.coach.flame.jpa.repository

import com.coach.flame.jpa.AbstractHelperTest
import com.coach.flame.jpa.entity.ClientStatus
import com.coach.flame.jpa.entity.MeasureConfig
import com.coach.flame.jpa.entity.maker.*
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

        val clientToTest = ClientBuilder.maker()
            .but(with(ClientMaker.clientType, clientType),
                with(ClientMaker.user, userMaker.make())
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
    fun `test update client and check client measure weights data`() {

        val clientToTest = ClientBuilder.maker()
            .but(with(ClientMaker.clientType, clientType),
                with(ClientMaker.user, userMaker.make())
            ).make()

        getClientRepository().saveAndFlush(clientToTest)

        entityManager.flush()
        entityManager.clear()

        val client = getClientRepository().findAll().first()

        val measureWeight1 = ClientMeasureWeightBuilder.default()
        val measureWeight2 = ClientMeasureWeightBuilder.default()

        client.clientMeasureWeight.add(measureWeight1)
        client.clientMeasureWeight.add(measureWeight2)

        getClientRepository().saveAndFlush(client)

        entityManager.flush()
        entityManager.clear()

        then(client.id).isNotNull
        then(client.clientMeasureWeight).hasSize(2)
        then(client.clientMeasureWeight.first().id).isNotNull

    }

    @Test
    fun `test get clients with coach`() {

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
    fun `test get clients for coach`() {

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
        val client4 = getClientRepository()
            .saveAndFlush(clientMaker.but(with(ClientMaker.clientType, clientType)).make())

        entityManager.flush()
        entityManager.clear()

        then(getClientRepository().findAll()).hasSize(5)
        then(getCoachRepository().findAll()).hasSize(2)

        client0.coach = coach0
        client0.clientStatus = ClientStatus.PENDING

        client1.coach = coach0
        client1.clientStatus = ClientStatus.ACCEPTED

        client4.coach = coach1
        client4.clientStatus = ClientStatus.ACCEPTED

        getClientRepository().saveAll(listOf(client0, client1, client4))

        entityManager.flush()
        entityManager.clear()

        val result = getClientRepository().findClientsForCoach(coach0.uuid.toString())
            .map { it.uuid }

        then(result).hasSize(4)
        then(result).contains(client0.uuid)
        then(result).contains(client1.uuid)
        then(result).contains(client2.uuid)
        then(result).contains(client3.uuid)
        then(result).doesNotContain(client4.uuid)

    }

    @Test
    fun `test create client with default values`() {

        val clientToTest = ClientBuilder.maker()
            .but(with(ClientMaker.clientType, clientType)).make()

        getClientRepository().saveAndFlush(clientToTest)

        entityManager.flush()
        entityManager.clear()

        val client = getClientRepository().findAll().first()

        then(client.id).isNotNull
        then(client.weight).isEqualTo(0.0f)
        then(client.height).isEqualTo(0.0f)
        then(client.measureConfig).isEqualTo(MeasureConfig.KG_CM)

    }

    @Test
    fun `test create client with default values set up`() {

        val clientToTest = ClientBuilder.maker()
            .but(with(ClientMaker.clientType, clientType),
                with(ClientMaker.height, 70.0787f),
                with(ClientMaker.weight, 173.1f),
                with(ClientMaker.measureConfig, MeasureConfig.LBS_IN)).make()

        getClientRepository().saveAndFlush(clientToTest)

        entityManager.flush()
        entityManager.clear()

        val client = getClientRepository().findAll().first()

        then(client.id).isNotNull
        then(client.height).isEqualTo(70.0787f)
        then(client.weight).isEqualTo(173.1f)
        then(client.measureConfig).isEqualTo(MeasureConfig.LBS_IN)

    }

}
