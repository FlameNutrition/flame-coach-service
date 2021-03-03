package com.coach.flame.client

import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.ClientDtoMaker
import com.coach.flame.domain.CountryDto
import com.coach.flame.domain.GenderDto
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
import org.springframework.dao.DataIntegrityViolationException
import java.time.LocalDate
import java.util.*

@ExtendWith(MockKExtension::class)
class ClientServiceImplTest {

    @MockK
    private lateinit var clientRepository: ClientRepository

    @MockK
    private lateinit var clientTypeRepository: ClientTypeRepository

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
            .isInstanceOf(ClientNotFoundException::class.java)
            .hasMessageContaining("Could not found any client with uuid: $uuid")
        verify(exactly = 0) { clientDtoConverter.convert(any()) }
        verify(exactly = 1) { clientRepository.findByUuid(uuid) }

    }

    @Test
    fun `register a new client`() {

        // given
        val preClientDto = clientDtoMaker
            .but(with(ClientDtoMaker.birthday, null as LocalDate?))
            .but(with(ClientDtoMaker.phoneCode, null as String?))
            .but(with(ClientDtoMaker.phoneNumber, null as String?))
            .but(with(ClientDtoMaker.country, null as CountryDto?))
            .but(with(ClientDtoMaker.gender, null as GenderDto?))
            .make()

        val entityClient = clientMaker
            .but(
                with(ClientMaker.firstname, preClientDto.firstName),
                with(ClientMaker.lastname, preClientDto.lastName),
                with(
                    ClientMaker.user, make(
                        a(
                            UserMaker.User,
                            with(UserMaker.email, preClientDto.loginInfo!!.username),
                            with(UserMaker.password, preClientDto.loginInfo!!.password)
                        )
                    )
                )
            )
            .make()
        every { clientTypeRepository.getByType(any()) } returns entityClient.clientType
        every { clientRepository.saveAndFlush(any()) } returns entityClient
        every { clientDtoConverter.convert(any()) } returns preClientDto

        // when
        val postClientDto = classToTest.registerClient(preClientDto)

        // then
        verify(exactly = 1) { clientDtoConverter.convert(any()) }
        then(postClientDto.loginInfo).isNotNull
        then(postClientDto.firstName).isEqualTo(entityClient.firstName)
        then(postClientDto.lastName).isEqualTo(entityClient.lastName)
        then(postClientDto.loginInfo!!.username).isEqualTo(preClientDto.loginInfo!!.username)
        then(postClientDto.loginInfo!!.password).isEqualTo(preClientDto.loginInfo!!.password)
        then(postClientDto.loginInfo!!.expirationDate).isNotNull
        then(postClientDto.loginInfo!!.token).isNotNull

    }

    @Test
    fun `register a new client duplicated`() {

        // given
        val entityClient = clientMaker.make()
        val clientDto = clientDtoMaker.make()
        every { clientTypeRepository.getByType(any()) } returns entityClient.clientType
        every { clientRepository.saveAndFlush(any()) } throws DataIntegrityViolationException("SQL ERROR!")

        // when
        val exception = catchThrowable { classToTest.registerClient(clientDto) }

        // then
        verify(exactly = 0) { clientDtoConverter.convert(any()) }
        then(exception)
            .isInstanceOf(ClientRegisterDuplicateException::class.java)
            .hasMessageContaining("The following client already exists")

    }

    @Test
    fun `register a new client but raised a exception`() {

        // given
        val entityClient = clientMaker.make()
        val clientDto = clientDtoMaker.make()
        every { clientTypeRepository.getByType(any()) } returns entityClient.clientType
        every { clientRepository.saveAndFlush(any()) } throws RuntimeException("Something wrong happened!")

        // when
        val exception = catchThrowable { classToTest.registerClient(clientDto) }

        // then
        verify(exactly = 0) { clientDtoConverter.convert(any()) }
        then(exception)
            .isInstanceOf(ClientRegisterException::class.java)
            .hasMessageContaining("Problem occurred when try to register a new client")

    }

}