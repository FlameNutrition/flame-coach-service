package com.coach.flame.client

import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.ClientDtoMaker
import com.coach.flame.domain.converters.ClientDtoConverter
import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.ClientMaker
import com.coach.flame.jpa.entity.UserMaker
import com.coach.flame.jpa.repository.ClientRepository
import com.coach.flame.jpa.repository.ClientTypeRepository
import com.natpryce.makeiteasy.MakeItEasy.*
import com.natpryce.makeiteasy.Maker
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class ClientServiceImplTest {

    @MockK
    private lateinit var clientRepository: ClientRepository

    @MockK
    private lateinit var clientDtoConverter: ClientDtoConverter

    @InjectMockKs
    private lateinit var classToTest: ClientServiceImpl

    private lateinit var clientDtoMaker: Maker<ClientDto>

    private lateinit var clientMaker: Maker<Client>

    @BeforeEach
    fun setUp() {
        clientDtoMaker = an(ClientDtoMaker.ClientDto)
        clientMaker = an(ClientMaker.Client)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `get valid client`() {

        // given
        val uuid = UUID.randomUUID()
        val client = clientMaker.make()
        val clientDtoConverted = clientDtoMaker.make()
        every { clientRepository.findByUuid(uuid) } returns client
        every { clientDtoConverter.convert(client) } returns clientDtoConverted

        // when
        val clientDto = classToTest.getClient(uuid)

        // then
        then(clientDto).isNotNull
        verify(exactly = 1) { clientDtoConverter.convert(client) }
        verify(exactly = 1) { clientRepository.findByUuid(uuid) }

    }

    @Test
    fun `get invalid client`() {

        // given
        val uuid = UUID.randomUUID()
        every { clientRepository.findByUuid(uuid) } returns null

        // when
        val thrown = catchThrowable { classToTest.getClient(uuid) }

        //then
        then(thrown)
            .isInstanceOf(ClientNotFound::class.java)
            .hasMessageContaining("Could not found any client with uuid: $uuid")
        verify(exactly = 0) { clientDtoConverter.convert(any()) }
        verify(exactly = 1) { clientRepository.findByUuid(uuid) }

    }

}