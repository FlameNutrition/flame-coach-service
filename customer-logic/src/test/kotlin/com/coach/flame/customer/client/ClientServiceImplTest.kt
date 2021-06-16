package com.coach.flame.customer.client

import com.coach.flame.customer.CustomerNotFoundException
import com.coach.flame.domain.ClientStatusDto
import com.coach.flame.domain.maker.CoachDtoBuilder
import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.ClientStatus
import com.coach.flame.jpa.entity.maker.ClientBuilder
import com.coach.flame.jpa.entity.maker.ClientMaker
import com.coach.flame.jpa.entity.maker.CoachBuilder
import com.coach.flame.jpa.entity.maker.CoachMaker
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

        val client0 = ClientBuilder.maker()
            .but(with(ClientMaker.clientStatus, ClientStatus.ACCEPTED))
            .make()
        val client1 = ClientBuilder.maker()
            .but(with(ClientMaker.clientStatus, ClientStatus.PENDING))
            .make()
        val client2 = ClientBuilder.maker()
            .but(with(ClientMaker.clientStatus, ClientStatus.AVAILABLE))
            .make()
        val client3 = ClientBuilder.default()
        every { clientRepository.findAll() } returns listOf(client0, client1, client2, client3)

        val result = classToTest.getAllClients()

        then(result).isNotEmpty
        then(result).hasSize(4)

    }

    @Test
    fun `test get all clients with same coach`() {

        val coach = CoachDtoBuilder.default()
        val client0 = ClientBuilder.maker()
            .but(with(ClientMaker.clientStatus, ClientStatus.ACCEPTED),
                with(ClientMaker.coach, CoachBuilder
                    .maker()
                    .but(with(CoachMaker.uuid, coach.identifier))
                    .make()))
            .make()
        val client1 = ClientBuilder.maker()
            .but(with(ClientMaker.clientStatus, ClientStatus.ACCEPTED),
                with(ClientMaker.coach, CoachBuilder
                    .maker()
                    .but(with(CoachMaker.uuid, coach.identifier))
                    .make()))
            .make()
        val client2 = ClientBuilder.maker()
            .but(with(ClientMaker.clientStatus, ClientStatus.ACCEPTED),
                with(ClientMaker.coach, CoachBuilder
                    .maker()
                    .but(with(CoachMaker.uuid, coach.identifier))
                    .make()))
            .make()
        every { clientRepository.findClientsWithCoach(coach.identifier) } returns listOf(client0, client1, client2)

        val result = classToTest.getAllClientsFromCoach(coach.identifier)

        then(result).isNotEmpty
        then(result).hasSize(3)

    }

    @Test
    fun `test get all clients for a coach`() {

        val uuidCoach = UUID.randomUUID()
        val client0 = ClientBuilder.maker()
            .but(with(ClientMaker.clientStatus, ClientStatus.AVAILABLE))
            .make()
        val client1 = ClientBuilder.maker()
            .but(with(ClientMaker.clientStatus, ClientStatus.AVAILABLE))
            .make()
        every { clientRepository.findClientsForCoach(uuidCoach.toString()) } returns listOf(client0, client1)

        val result = classToTest.getAllClientsForCoach(uuidCoach)

        then(result).isNotEmpty
        then(result).hasSize(2)

    }

    @Test
    fun `test update status client`() {

        val uuid = UUID.randomUUID()
        val clientSlot = slot<Client>()
        val client = ClientBuilder.maker()
            .but(with(ClientMaker.clientStatus, ClientStatus.AVAILABLE))
            .make()

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
        val client = ClientBuilder.maker()
            .but(with(ClientMaker.clientStatus, ClientStatus.AVAILABLE))
            .make()
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
        val client = ClientBuilder.maker()
            .but(with(ClientMaker.clientStatus, ClientStatus.AVAILABLE))
            .make()

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
        val client = ClientBuilder.maker()
            .but(with(ClientMaker.clientStatus, ClientStatus.ACCEPTED))
            .make()

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
