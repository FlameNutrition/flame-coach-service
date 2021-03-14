package com.coach.flame.customer.coach

import com.coach.flame.customer.CustomerService
import com.coach.flame.customer.client.ClientService
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

    @MockK
    private lateinit var clientsService: ClientService

    @InjectMockKs
    private lateinit var classToTest: CoachServiceImpl

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test get coach only with client with_coach`() {

        val uuid = UUID.randomUUID()
        val client0 = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.ACCEPTED))
            .make()
        val client1 = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.ACCEPTED))
            .make()
        val client2 = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.ACCEPTED))
            .make()
        val client3 = ClientDtoBuilder.default()
        val coach = CoachDtoBuilder.maker()
            .but(with(CoachDtoMaker.identifier, uuid),
                with(CoachDtoMaker.listOfClients, setOf(client0, client1, client2, client3)))
            .make()
        every { customerService.getCustomer(uuid, CustomerTypeDto.COACH) } returns coach

        val coachResult = classToTest.getCoachWithClientsAccepted(uuid)

        then(coachResult.identifier).isEqualTo(uuid)
        then(coachResult.listOfClients).isNotEmpty
        then(coachResult.listOfClients).hasSize(3)

    }

    @Test
    fun `test get coach only with client active and avoid duplications`() {

        val uuid = UUID.randomUUID()
        val clientsUUID = UUID.randomUUID()
        val client0 = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.ACCEPTED),
                with(ClientDtoMaker.identifier, clientsUUID))
            .make()
        val client1 = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.ACCEPTED),
                with(ClientDtoMaker.identifier, clientsUUID))
            .make()
        val coach = CoachDtoBuilder.maker()
            .but(with(CoachDtoMaker.identifier, uuid),
                with(CoachDtoMaker.listOfClients, setOf(client0, client1)))
            .make()
        every { customerService.getCustomer(uuid, CustomerTypeDto.COACH) } returns coach

        val coachResult = classToTest.getCoachWithClientsAccepted(uuid)

        then(coachResult.identifier).isEqualTo(uuid)
        then(coachResult.listOfClients).isNotEmpty
        then(coachResult.listOfClients).hasSize(1)

    }

    @Test
    fun `test get all clients for coach`() {

        val uuid = UUID.randomUUID()

        val client0 = ClientDtoBuilder.maker().but(with(ClientDtoMaker.clientStatus, ClientStatusDto.PENDING)).make()
        val client1 = ClientDtoBuilder.maker().but(with(ClientDtoMaker.clientStatus, ClientStatusDto.ACCEPTED)).make()
        val client2 = ClientDtoBuilder.maker().but(with(ClientDtoMaker.clientStatus, ClientStatusDto.PENDING)).make()
        val client3 = ClientDtoBuilder.maker().but(with(ClientDtoMaker.clientStatus, ClientStatusDto.ACCEPTED)).make()
        val client4 = ClientDtoBuilder.maker().but(with(ClientDtoMaker.clientStatus, ClientStatusDto.ACCEPTED)).make()

        val coach = CoachDtoBuilder.maker()
            .but(with(CoachDtoMaker.listOfClients, setOf(client0, client1, client2, client3, client4)),
                with(CoachDtoMaker.identifier, uuid)).make()

        val clientAvailable0 = ClientDtoBuilder.default()
        val clientAvailable1 = ClientDtoBuilder.default()
        val clientAvailable2 = ClientDtoBuilder.default()
        val clientAvailable3 = ClientDtoBuilder.default()

        every { customerService.getCustomer(uuid, CustomerTypeDto.COACH) } returns coach
        every { clientsService.getAllClientsForCoach(uuid) } returns setOf(clientAvailable0, clientAvailable1, clientAvailable2,
            clientAvailable3, client0, client1, client2, client3, client4)

        val result = classToTest.getCoachWithClientsAvailable(uuid)

        then(result.identifier).isEqualTo(uuid)
        then(result.listOfClients).hasSize(9)

    }

}