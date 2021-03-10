package com.coach.flame.client

import com.coach.flame.domain.*
import com.coach.flame.domain.converters.ClientDtoConverter
import com.coach.flame.domain.converters.CountryDtoConverter
import com.coach.flame.domain.converters.GenderDtoConverter
import com.coach.flame.jpa.entity.*
import com.coach.flame.jpa.repository.ClientRepository
import com.coach.flame.jpa.repository.ClientTypeRepository
import com.coach.flame.jpa.repository.UserRepository
import com.coach.flame.jpa.repository.UserSessionRepository
import com.natpryce.makeiteasy.MakeItEasy.*
import com.natpryce.makeiteasy.Maker
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.dao.DataIntegrityViolationException
import java.lang.IllegalStateException
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
class ClientServiceImplTest {

    @MockK
    private lateinit var clientRepository: ClientRepository

    @MockK
    private lateinit var clientTypeRepository: ClientTypeRepository

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var userSessionRepository: UserSessionRepository

    @SpyK
    private var genderDtoConverter: GenderDtoConverter = GenderDtoConverter()

    @SpyK
    private var countryDtoConverter: CountryDtoConverter = CountryDtoConverter()

    @SpyK
    private var clientDtoConverter: ClientDtoConverter = ClientDtoConverter(countryDtoConverter, genderDtoConverter)

    @InjectMockKs
    private lateinit var classToTest: ClientServiceImpl

    private lateinit var clientDtoMaker: Maker<ClientDto>
    private lateinit var clientMaker: Maker<Client>
    private lateinit var userSessionMaker: Maker<UserSession>
    private lateinit var userMaker: Maker<User>
    private lateinit var loginInfoMaker: Maker<LoginInfoDto>

    @BeforeEach
    fun setUp() {
        clientDtoMaker = an(ClientDtoMaker.ClientDto)
        clientMaker = an(ClientMaker.Client)
        userSessionMaker = an(UserSessionMaker.UserSession)
        userMaker = an(UserMaker.User)
        loginInfoMaker = an(LoginInfoDtoMaker.LoginInfoDto)
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

        val expirationDate = LocalDateTime.now()
        val token = UUID.randomUUID()
        val entityClient = clientMaker
            .but(
                with(ClientMaker.firstname, preClientDto.firstName),
                with(ClientMaker.lastname, preClientDto.lastName),
                with(ClientMaker.userSession,
                    make(a(UserSessionMaker.UserSession,
                        with(UserSessionMaker.token, token),
                        with(UserSessionMaker.expirationDate, expirationDate)))),
                with(ClientMaker.user,
                    make(a(UserMaker.User,
                        with(UserMaker.email, preClientDto.loginInfo!!.username),
                        with(UserMaker.password, preClientDto.loginInfo!!.password)))))
            .make()
        val clientCaptorSlot = slot<Client>()
        every { clientTypeRepository.getByType(any()) } returns entityClient.clientType
        every { clientRepository.saveAndFlush(capture(clientCaptorSlot)) } returns entityClient
        every { clientDtoConverter.convert(entityClient) } returns preClientDto

        // when
        val postClientDto = classToTest.registerClient(preClientDto)

        // then
        verify(exactly = 1) { clientDtoConverter.convert(any()) }
        then(clientCaptorSlot.isCaptured).isTrue
        then(clientCaptorSlot.captured.user.userSession.token).isNotNull
        then(clientCaptorSlot.captured.user.userSession.expirationDate).isBetween(expirationDate,
            expirationDate.plusHours(2).plusMinutes(10))
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
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageContaining("Something wrong happened!")

    }

    @ParameterizedTest(name = "[{index}] register new client missing parameter: {0}")
    @MethodSource("userRegisterMandatoryParameters")
    fun `register a new client but missing mandatory params`(missingParam: String) {

        var expectedMessage = "$missingParam is a mandatory parameter"
        val clientDto = when (missingParam) {
            "clientType" -> clientDtoMaker
                .but(with(ClientDtoMaker.clientType, null as ClientTypeDto?))
                .make()
            "firstName" -> clientDtoMaker
                .but(with(ClientDtoMaker.firstName, null as String?))
                .make()
            "lastName" -> clientDtoMaker
                .but(with(ClientDtoMaker.lastName, null as String?))
                .make()
            "username" -> {
                expectedMessage = "loginInfo->$missingParam is a mandatory parameter"
                clientDtoMaker
                    .but(with(ClientDtoMaker.loginInfo, loginInfoMaker
                        .but(with(LoginInfoDtoMaker.username, null as String?))
                        .make()))
                    .make()
            }
            "password" -> {
                expectedMessage = "loginInfo->$missingParam is a mandatory parameter"
                clientDtoMaker
                    .but(with(ClientDtoMaker.loginInfo, loginInfoMaker
                        .but(with(LoginInfoDtoMaker.password, null as String?))
                        .make()))
                    .make()
            }
            "loginInfo" -> {
                expectedMessage = "loginInfo->username is a mandatory parameter"
                clientDtoMaker
                    .but(with(ClientDtoMaker.loginInfo, null as LoginInfoDto?))
                    .make()
            }
            else -> clientDtoMaker.make()
        }

        // when
        val exception = catchThrowable { classToTest.registerClient(clientDto) }

        // then
        verify(exactly = 0) { clientDtoConverter.convert(any()) }
        then(exception)
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining(expectedMessage)

    }

    @Test
    fun `get a new client session`() {

        // given
        val username = "test@gmail.com"
        val password = "12345"
        val userSession = slot<UserSession>()
        val actualDate = LocalDateTime.now()
        val entityUserSession = userSessionMaker
            .but(with(UserSessionMaker.expirationDate, actualDate))
            .make()
        val entityClient = clientMaker
            .but(with(ClientMaker.user,
                make(a(UserMaker.User,
                    with(UserMaker.email, "test@gmail.com"),
                    with(UserMaker.password, "12345")))))
            .but(with(ClientMaker.userSession, entityUserSession))
            .make()
        val user = userMaker
            .but(with(UserMaker.email, "test@gmail.com"))
            .but(with(UserMaker.password, "12345"))
            .but(with(UserMaker.client, entityClient))
            .make()

        every { userRepository.findUserByEmailAndPassword("test@gmail.com", "12345") } returns user
        every { userSessionRepository.saveAndFlush(capture(userSession)) } returns mockk()

        // when
        val response = classToTest.getNewClientSession(username, password)

        // then
        verify(exactly = 1) { userRepository.findUserByEmailAndPassword("test@gmail.com", "12345") }
        verify(exactly = 1) { userSessionRepository.saveAndFlush(any()) }
        verify(exactly = 1) { clientDtoConverter.convert(any()) }
        then(userSession.isCaptured).isTrue
        then(userSession.captured.token).isNotNull
        then(userSession.captured.expirationDate).isNotEqualTo(actualDate)
        then(response.firstName).isNotEmpty
        then(response.lastName).isNotEmpty
        then(response.loginInfo?.username).isEqualTo("test@gmail.com")
        then(response.loginInfo?.password).isEqualTo("******")
        then(response.loginInfo?.expirationDate).isEqualTo(userSession.captured.expirationDate)
        then(response.loginInfo?.token).isEqualTo(userSession.captured.token)

    }

    @Test
    fun `get a new client session but username or password is invalid`() {

        // given
        every { userRepository.findUserByEmailAndPassword("INVALID", "12345") } returns null

        // when
        val exception1 = catchThrowable { classToTest.getNewClientSession("INVALID", "12345") }

        // then
        verify(exactly = 1) { userRepository.findUserByEmailAndPassword("INVALID", "12345") }
        verify(exactly = 0) { clientDtoConverter.convert(any()) }
        verify(exactly = 0) { userSessionRepository.saveAndFlush(any()) }
        then(exception1)
            .isInstanceOf(ClientUsernameOrPasswordException::class.java)
            .hasMessageContaining("Username or password invalid")

        // given
        every { userRepository.findUserByEmailAndPassword("test@gmail.com", "INVALID") } returns null

        // when
        val exception2 = catchThrowable { classToTest.getNewClientSession("test@gmail.com", "INVALID") }

        // then
        verify(exactly = 1) { userRepository.findUserByEmailAndPassword("test@gmail.com", "INVALID") }
        verify(exactly = 0) { clientDtoConverter.convert(any()) }
        verify(exactly = 0) { userSessionRepository.saveAndFlush(any()) }
        then(exception2)
            .isInstanceOf(ClientUsernameOrPasswordException::class.java)
            .hasMessageContaining("Username or password invalid")

    }

    // region Parameters

    companion object {
        @JvmStatic
        fun userRegisterMandatoryParameters(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("clientType"),
                Arguments.of("firstName"),
                Arguments.of("lastName"),
                Arguments.of("username"),
                Arguments.of("password"),
                Arguments.of("loginInfo"),
            )
        }
    }

    // endregion

}