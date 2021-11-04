package com.coach.flame.customer.client

import com.coach.flame.customer.CustomerNotFoundException
import com.coach.flame.domain.ClientStatusDto
import com.coach.flame.domain.maker.ClientDtoBuilder
import com.coach.flame.domain.maker.ClientDtoMaker
import com.coach.flame.domain.maker.CoachDtoBuilder
import com.coach.flame.domain.maker.CoachDtoMaker
import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.Client.Companion.toClient
import com.coach.flame.jpa.entity.ClientStatus
import com.coach.flame.jpa.entity.maker.CoachBuilder
import com.coach.flame.jpa.repository.ClientRepository
import com.coach.flame.jpa.repository.CoachRepository
import com.natpryce.makeiteasy.MakeItEasy.with
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class ClientServiceImplTest {

    @MockK
    private lateinit var clientRepository: ClientRepository

    @MockK
    private lateinit var coachRepository: CoachRepository

    @InjectMockKs
    private lateinit var classToTest: ClientServiceImpl

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test get all clients`() {

        val client0 = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.ACCEPTED))
            .make()
            .toClient()
        val client1 = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.PENDING))
            .make()
            .toClient()
        val client2 = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.AVAILABLE))
            .make()
            .toClient()
        val client3 = ClientDtoBuilder.makerWithLoginInfo()
            .make()
            .toClient()
        every { clientRepository.findAll() } returns listOf(client0, client1, client2, client3)

        val result = classToTest.getAllClients()

        then(result).isNotEmpty
        then(result).hasSize(4)

    }

    @Test
    fun `test get all clients with same coach`() {

        val coach = CoachDtoBuilder.default()
        val client0 = ClientDtoBuilder.makerWithLoginInfo()
            .but(
                with(ClientDtoMaker.clientStatus, ClientStatusDto.ACCEPTED),
                with(
                    ClientDtoMaker.coach, CoachDtoBuilder.makerWithLoginInfo()
                        .but(with(CoachDtoMaker.identifier, coach.identifier))
                        .make()
                )
            )
            .make()
            .toClient()

        val client1 = ClientDtoBuilder.makerWithLoginInfo()
            .but(
                with(ClientDtoMaker.clientStatus, ClientStatusDto.ACCEPTED),
                with(
                    ClientDtoMaker.coach, CoachDtoBuilder.makerWithLoginInfo()
                        .but(with(CoachDtoMaker.identifier, coach.identifier))
                        .make()
                )
            )
            .make()
            .toClient()
        val client2 = ClientDtoBuilder.makerWithLoginInfo()
            .but(
                with(ClientDtoMaker.clientStatus, ClientStatusDto.ACCEPTED),
                with(
                    ClientDtoMaker.coach, CoachDtoBuilder.makerWithLoginInfo()
                        .but(with(CoachDtoMaker.identifier, coach.identifier))
                        .make()
                )
            )
            .make()
            .toClient()
        every { clientRepository.getClientsWithCoach(coach.identifier) } returns listOf(client0, client1, client2)

        val result = classToTest.getAllClientsFromCoach(coach.identifier)

        then(result).isNotEmpty
        then(result).hasSize(3)

    }

    @Test
    fun `test get all clients for a coach`() {

        val uuidCoach = UUID.randomUUID()
        val client0 = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.AVAILABLE))
            .make()
            .toClient()
        val client1 = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.AVAILABLE))
            .make()
            .toClient()
        every { clientRepository.getClientsForCoach(uuidCoach.toString()) } returns listOf(client0, client1)

        val result = classToTest.getAllClientsForCoach(uuidCoach)

        then(result).isNotEmpty
        then(result).hasSize(2)

    }

    @Test
    fun `test update status client`() {

        val uuid = UUID.randomUUID()
        val clientSlot = slot<Client>()
        val client = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.AVAILABLE))
            .make()
            .toClient()

        every { clientRepository.findByUuid(uuid) } returns client
        every { clientRepository.save(capture(clientSlot)) } answers { clientSlot.captured }

        val result = classToTest.updateClientStatus(uuid, ClientStatusDto.PENDING)

        then(clientSlot.isCaptured).isTrue
        then(clientSlot.captured.clientStatus).isEqualTo(ClientStatus.PENDING)
        then(result.clientStatus).isEqualTo(ClientStatusDto.PENDING)

    }

    @Test
    fun `test update status client when client is invalid`() {

        val uuid = UUID.randomUUID()

        every { clientRepository.findByUuid(uuid) } returns null

        val result = catchThrowable { classToTest.updateClientStatus(uuid, ClientStatusDto.PENDING) }

        then(result)
            .isInstanceOf(CustomerNotFoundException::class.java)
            .hasMessageContaining("Could not find any client with uuid: $uuid.")
        verify(exactly = 1) { clientRepository.findByUuid(uuid) }
        verify(exactly = 0) { clientRepository.save(any()) }

    }

    @Test
    fun `test linked coach to client`() {

        val uuidClient = UUID.randomUUID()
        val uuidCoach = UUID.randomUUID()
        val clientSlot = slot<Client>()
        val client = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.AVAILABLE))
            .make()
            .toClient()
        val coach = CoachBuilder.default()

        every { clientRepository.findByUuid(uuidClient) } returns client
        every { coachRepository.findByUuid(uuidCoach) } returns coach
        every { clientRepository.save(capture(clientSlot)) } answers { clientSlot.captured }

        classToTest.linkCoach(uuidClient, uuidCoach)

        then(clientSlot.isCaptured).isTrue
        then(clientSlot.captured.coach).isNotNull

    }

    @Test
    fun `test linked coach to client when coach is invalid`() {

        val uuidClient = UUID.randomUUID()
        val uuidCoach = UUID.randomUUID()
        val client = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.AVAILABLE))
            .make()
            .toClient()

        every { clientRepository.findByUuid(uuidClient) } returns client
        every { coachRepository.findByUuid(uuidCoach) } returns null

        val result = catchThrowable { classToTest.linkCoach(uuidClient, uuidCoach) }

        then(result)
            .isInstanceOf(CustomerNotFoundException::class.java)
            .hasMessageContaining("Could not find any coach with uuid: $uuidCoach.")
        verify(exactly = 1) { clientRepository.findByUuid(uuidClient) }
        verify(exactly = 1) { coachRepository.findByUuid(uuidCoach) }
        verify(exactly = 0) { clientRepository.save(any()) }

    }

    @Test
    fun `test unlink coach from a client`() {

        val uuidClient = UUID.randomUUID()
        val clientSlot = slot<Client>()
        val client = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.ACCEPTED))
            .make()
            .toClient()

        every { clientRepository.findByUuid(uuidClient) } returns client
        every { clientRepository.save(capture(clientSlot)) } answers { clientSlot.captured }

        classToTest.unlinkCoach(uuidClient)

        then(clientSlot.isCaptured).isTrue
        then(clientSlot.captured.coach).isNull()
        then(clientSlot.captured.clientStatus).isEqualTo(ClientStatus.AVAILABLE)

    }

    @Test
    fun `test unlink coach from a client when client is invalid`() {

        val uuidClient = UUID.randomUUID()

        every { clientRepository.findByUuid(uuidClient) } returns null

        val result = catchThrowable { classToTest.unlinkCoach(uuidClient) }

        then(result)
            .isInstanceOf(CustomerNotFoundException::class.java)
            .hasMessageContaining("Could not find any client with uuid: $uuidClient.")
        verify(exactly = 1) { clientRepository.findByUuid(uuidClient) }
        verify(exactly = 0) { clientRepository.save(any()) }

    }

}
