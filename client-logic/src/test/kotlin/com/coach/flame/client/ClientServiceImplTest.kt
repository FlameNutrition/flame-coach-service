package com.coach.flame.client

import com.coach.flame.domain.ClientDtoGenerator
import com.coach.flame.domain.converters.ClientDtoConverter
import com.coach.flame.jpa.entity.ClientGenerator
import com.coach.flame.jpa.repository.ClientRepository
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.BDDAssertions
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
    private lateinit var clientDtoConverter: ClientDtoConverter

    @InjectMockKs
    private lateinit var classToTest: ClientServiceImpl

    @AfterEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `get valid client`() {

        // given
        val uuid = UUID.randomUUID()
        val client = ClientGenerator.Builder().build().nextObject()
        val clientDtoConverted = ClientDtoGenerator.Builder().build().nextObject()
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