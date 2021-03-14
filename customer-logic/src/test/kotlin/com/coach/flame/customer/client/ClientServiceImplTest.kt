package com.coach.flame.customer.client

import com.coach.flame.domain.CoachDtoBuilder
import com.coach.flame.domain.converters.ClientToClientDtoConverter
import com.coach.flame.domain.converters.CountryConfigToCountryDtoConverter
import com.coach.flame.domain.converters.GenderConfigToGenderDtoConverter
import com.coach.flame.jpa.entity.*
import com.coach.flame.jpa.repository.ClientRepository
import com.natpryce.makeiteasy.MakeItEasy.with
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class ClientServiceImplTest {

    @MockK
    private lateinit var clientRepository: ClientRepository

    @SpyK
    private var genderConfigToGenderDtoConverter: GenderConfigToGenderDtoConverter = GenderConfigToGenderDtoConverter()

    @SpyK
    private var countryConfigToCountryDtoConverter: CountryConfigToCountryDtoConverter =
        CountryConfigToCountryDtoConverter()

    @SpyK
    private var clientToClientDtoConverter: ClientToClientDtoConverter =
        ClientToClientDtoConverter(countryConfigToCountryDtoConverter, genderConfigToGenderDtoConverter)

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

        verify(exactly = 4) { clientToClientDtoConverter.convert(any()) }
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

        verify(exactly = 3) { clientToClientDtoConverter.convert(any()) }
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

        verify(exactly = 2) { clientToClientDtoConverter.convert(any()) }
        then(result).isNotEmpty
        then(result).hasSize(2)

    }

}