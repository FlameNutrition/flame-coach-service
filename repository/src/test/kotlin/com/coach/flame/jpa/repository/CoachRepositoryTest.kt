package com.coach.flame.jpa.repository

import com.coach.flame.jpa.AbstractHelperTest
import com.coach.flame.jpa.entity.ClientStatus
import com.coach.flame.jpa.entity.maker.ClientMaker
import com.coach.flame.jpa.entity.maker.ClientTypeMaker
import com.coach.flame.jpa.entity.maker.CoachMaker
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CoachRepositoryTest : AbstractHelperTest() {

    @Test
    fun `test get coach`() {

        val coach0 = getCoachRepository()
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
        then(getCoachRepository().findAll()).hasSize(1)

        client0.coach = coach0
        client0.clientStatus = ClientStatus.PENDING

        client1.coach = coach0
        client1.clientStatus = ClientStatus.ACCEPTED

        getClientRepository().saveAll(listOf(client0, client1))

        entityManager.flush()
        entityManager.clear()

        val coach = getCoachRepository().findByUuid(coach0.uuid)

        val listOfClientCoach0 = coach!!.clients.map { it.uuid }

        then(listOfClientCoach0).contains(client0.uuid)
        then(listOfClientCoach0).contains(client1.uuid)
        then(listOfClientCoach0).doesNotContain(client3.uuid)
        then(listOfClientCoach0).doesNotContain(client2.uuid)
        then(listOfClientCoach0).hasSize(2)

    }

}
