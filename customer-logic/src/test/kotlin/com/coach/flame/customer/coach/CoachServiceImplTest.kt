package com.coach.flame.customer.coach

import com.coach.flame.customer.CustomerService
import com.coach.flame.domain.*
import com.natpryce.makeiteasy.MakeItEasy.with
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class CoachServiceImplTest {

    @MockK
    private lateinit var customerService: CustomerService

    @InjectMockKs
    private lateinit var classToTest: CoachServiceImpl

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test get coach only with client active`() {

        val uuid = UUID.randomUUID()
        val client0 = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.WITH_COACH))
            .make()
        val client1 = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.WITH_COACH))
            .make()
        val client2 = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.WITH_COACH))
            .make()
        val client3 = ClientDtoBuilder.default()
        val coach = CoachDtoBuilder.maker()
            .but(with(CoachDtoMaker.identifier, uuid),
                with(CoachDtoMaker.listOfClients, setOf(client0, client1, client2, client3)))
            .make()
        every { customerService.getCustomer(uuid, CustomerTypeDto.COACH) } returns coach

        val coachResult = classToTest.getCoachWithClientsAvailable(uuid)

        then(coachResult.identifier).isEqualTo(uuid)
        then(coachResult.listOfClients).isNotEmpty
        then(coachResult.listOfClients).hasSize(3)

    }

    @Test
    fun `test get coach only with client active and avoid duplications`() {

        val uuid = UUID.randomUUID()
        val clientsUUID = UUID.randomUUID()
        val client0 = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.WITH_COACH),
                with(ClientDtoMaker.identifier, clientsUUID))
            .make()
        val client1 = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.WITH_COACH),
                with(ClientDtoMaker.identifier, clientsUUID))
            .make()
        val coach = CoachDtoBuilder.maker()
            .but(with(CoachDtoMaker.identifier, uuid),
                with(CoachDtoMaker.listOfClients, setOf(client0, client1)))
            .make()
        every { customerService.getCustomer(uuid, CustomerTypeDto.COACH) } returns coach

        val coachResult = classToTest.getCoachWithClientsAvailable(uuid)

        then(coachResult.identifier).isEqualTo(uuid)
        then(coachResult.listOfClients).isNotEmpty
        then(coachResult.listOfClients).hasSize(1)

    }

}