package com.coach.flame.customer

import com.coach.flame.domain.*
import com.coach.flame.domain.converters.ClientDtoConverter
import com.coach.flame.domain.converters.CoachDtoConverter
import com.coach.flame.domain.converters.CountryDtoConverter
import com.coach.flame.domain.converters.GenderDtoConverter
import com.coach.flame.jpa.entity.*
import com.coach.flame.jpa.repository.*
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
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
class CustomerServiceImplTest {

    @MockK
    private lateinit var clientRepository: ClientRepository

    @MockK
    private lateinit var clientTypeRepository: ClientTypeRepository

    @MockK
    private lateinit var coachRepository: CoachRepository

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

    @SpyK
    private var coachDtoConverter: CoachDtoConverter = CoachDtoConverter(countryDtoConverter, genderDtoConverter)

    @InjectMockKs
    private lateinit var classToTest: CustomerServiceImpl

    private lateinit var clientDtoMaker: Maker<ClientDto>
    private lateinit var clientMaker: Maker<Client>
    private lateinit var coachMaker: Maker<Coach>
    private lateinit var userSessionMaker: Maker<UserSession>
    private lateinit var userMaker: Maker<User>
    private lateinit var loginInfoMaker: Maker<LoginInfoDto>
    private lateinit var clientTypeMaker: Maker<ClientType>

    @BeforeEach
    fun setUp() {
        clientDtoMaker = an(ClientDtoMaker.ClientDto)
        clientMaker = an(ClientMaker.Client)
        coachMaker = an(CoachMaker.Coach)
        userSessionMaker = an(UserSessionMaker.UserSession)
        userMaker = an(UserMaker.User)
        loginInfoMaker = an(LoginInfoDtoMaker.LoginInfoDto)
        clientTypeMaker = an(ClientTypeMaker.ClientType)
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
        every { clientRepository.findByUuid(uuid) } returns client

        // when
        val clientDto = classToTest.getCustomer(uuid, CustomerTypeDto.CLIENT)

        // then
        then(clientDto).isNotNull
        verify(exactly = 1) { clientDtoConverter.convert(client) }
        verify(exactly = 1) { clientRepository.findByUuid(uuid) }

    }

    @Test
    fun `get valid coach`() {

        // given
        val uuid = UUID.randomUUID()
        val coach = coachMaker.make()
        every { coachRepository.findByUuid(uuid) } returns coach

        // when
        val clientDto = classToTest.getCustomer(uuid, CustomerTypeDto.COACH)

        // then
        then(clientDto).isNotNull
        verify(exactly = 1) { coachDtoConverter.convert(coach) }
        verify(exactly = 1) { coachRepository.findByUuid(uuid) }

    }

    @Test
    fun `get invalid client`() {

        // given
        val uuid = UUID.randomUUID()
        every { clientRepository.findByUuid(uuid) } returns null

        // when
        val thrown = catchThrowable { classToTest.getCustomer(uuid, CustomerTypeDto.CLIENT) }

        //then
        then(thrown)
            .isInstanceOf(CustomerNotFoundException::class.java)
            .hasMessageContaining("Could not found any client with uuid: $uuid")
        verify(exactly = 0) { clientDtoConverter.convert(any()) }
        verify(exactly = 1) { clientRepository.findByUuid(uuid) }

    }

    @Test
    fun `get invalid coach`() {

        // given
        val uuid = UUID.randomUUID()
        every { coachRepository.findByUuid(uuid) } returns null

        // when
        val thrown = catchThrowable { classToTest.getCustomer(uuid, CustomerTypeDto.COACH) }

        //then
        then(thrown)
            .isInstanceOf(CustomerNotFoundException::class.java)
            .hasMessageContaining("Could not found any coach with uuid: $uuid")
        verify(exactly = 0) { coachDtoConverter.convert(any()) }
        verify(exactly = 1) { coachRepository.findByUuid(uuid) }

    }

    @Test
    fun `register a new client`() {

        // given
        val expectedExpirationDate = LocalDateTime.now()
        val expectedToken = UUID.randomUUID()
        val preClientDto = clientDtoMaker
            .but(with(ClientDtoMaker.loginInfo, loginInfoMaker
                .but(with(LoginInfoDtoMaker.expirationDate, expectedExpirationDate),
                    with(LoginInfoDtoMaker.token, expectedToken))
                .make()))
            .make()

        val clientCaptorSlot = slot<Client>()
        every { clientTypeRepository.getByType("CLIENT") } returns clientTypeMaker.make()
        every { clientRepository.save(capture(clientCaptorSlot)) } answers { clientCaptorSlot.captured }

        // when
        val postClientDto = classToTest.registerCustomer(preClientDto) as ClientDto

        // then
        verify(exactly = 1) { clientDtoConverter.convert(any()) }
        verify(exactly = 1) { clientRepository.save(any()) }
        verify(exactly = 0) { coachRepository.save(any()) }
        then(clientCaptorSlot.isCaptured).isTrue
        then(postClientDto.loginInfo).isNotNull
        then(postClientDto.firstName).isEqualTo(clientCaptorSlot.captured.firstName)
        then(postClientDto.lastName).isEqualTo(clientCaptorSlot.captured.lastName)
        then(postClientDto.customerType).isEqualTo(CustomerTypeDto.CLIENT)
        then(postClientDto.loginInfo!!.username).isEqualTo(clientCaptorSlot.captured.user.email)
        then(postClientDto.loginInfo!!.password).isEqualTo("******")
        then(postClientDto.loginInfo!!.expirationDate).isEqualTo(clientCaptorSlot.captured.user.userSession.expirationDate)
        then(postClientDto.loginInfo!!.token).isEqualTo(clientCaptorSlot.captured.user.userSession.token)
    }

    @Test
    fun `register a new coach`() {

        // given
        val expectedExpirationDate = LocalDateTime.now()
        val expectedToken = UUID.randomUUID()
        val preClientDto = clientDtoMaker
            .but(with(ClientDtoMaker.customerType, CustomerTypeDto.COACH),
                with(ClientDtoMaker.loginInfo, loginInfoMaker
                    .but(with(LoginInfoDtoMaker.expirationDate, expectedExpirationDate),
                        with(LoginInfoDtoMaker.token, expectedToken))
                    .make()))
            .make()

        val clientCaptorSlot = slot<Coach>()
        every { clientTypeRepository.getByType("COACH") } returns clientTypeMaker
            .but(with(ClientTypeMaker.type, "COACH"))
            .make()
        every { coachRepository.save(capture(clientCaptorSlot)) } answers { clientCaptorSlot.captured }

        // when
        val postClientDto = classToTest.registerCustomer(preClientDto) as CoachDto

        // then
        verify(exactly = 1) { coachDtoConverter.convert(any()) }
        verify(exactly = 0) { clientRepository.save(any()) }
        verify(exactly = 1) { coachRepository.save(any()) }
        then(clientCaptorSlot.isCaptured).isTrue
        then(postClientDto.loginInfo).isNotNull
        then(postClientDto.firstName).isEqualTo(clientCaptorSlot.captured.firstName)
        then(postClientDto.lastName).isEqualTo(clientCaptorSlot.captured.lastName)
        then(postClientDto.customerType).isEqualTo(CustomerTypeDto.COACH)
        then(postClientDto.loginInfo!!.username).isEqualTo(clientCaptorSlot.captured.user.email)
        then(postClientDto.loginInfo!!.password).isEqualTo("******")
        then(postClientDto.loginInfo!!.expirationDate).isEqualTo(clientCaptorSlot.captured.user.userSession.expirationDate)
        then(postClientDto.loginInfo!!.token).isEqualTo(clientCaptorSlot.captured.user.userSession.token)
    }


    @Test
    fun `register a new client duplicated`() {

        // given
        val entityClient = clientMaker.make()
        val clientDto = clientDtoMaker
            .but(with(ClientDtoMaker.loginInfo, loginInfoMaker.make()))
            .make()
        every { clientTypeRepository.getByType(any()) } returns entityClient.clientType
        every { clientRepository.save(any()) } throws DataIntegrityViolationException("SQL ERROR!")

        // when
        val exception = catchThrowable { classToTest.registerCustomer(clientDto) }

        // then
        verify(exactly = 0) { clientDtoConverter.convert(any()) }
        then(exception)
            .isInstanceOf(CustomerRegisterDuplicateException::class.java)
            .hasMessageContaining("The following customer already exists")

    }

    @Test
    fun `register a new client but raised a exception`() {

        // given
        val entityClient = clientMaker.make()
        val clientDto = clientDtoMaker
            .but(with(ClientDtoMaker.loginInfo, loginInfoMaker.make()))
            .make()
        every { clientTypeRepository.getByType(any()) } returns entityClient.clientType
        every { clientRepository.save(any()) } throws RuntimeException("Something wrong happened!")

        // when
        val exception = catchThrowable { classToTest.registerCustomer(clientDto) }

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
            "loginInfo" -> {
                expectedMessage = "loginInfo->username is a mandatory parameter"
                clientDtoMaker
                    .but(with(ClientDtoMaker.loginInfo, null as LoginInfoDto?))
                    .make()
            }
            else -> clientDtoMaker.make()
        }

        // when
        val exception = catchThrowable { classToTest.registerCustomer(clientDto) }

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
        every { userSessionRepository.save(capture(userSession)) } returns mockk()

        // when
        val response = classToTest.getNewCustomerSession(username, password) as ClientDto

        // then
        verify(exactly = 1) { userRepository.findUserByEmailAndPassword("test@gmail.com", "12345") }
        verify(exactly = 1) { userSessionRepository.save(any()) }
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
        val exception1 = catchThrowable { classToTest.getNewCustomerSession("INVALID", "12345") }

        // then
        verify(exactly = 1) { userRepository.findUserByEmailAndPassword("INVALID", "12345") }
        verify(exactly = 0) { clientDtoConverter.convert(any()) }
        verify(exactly = 0) { userSessionRepository.saveAndFlush(any()) }
        then(exception1)
            .isInstanceOf(CustomerUsernameOrPasswordException::class.java)
            .hasMessageContaining("Username or password invalid")

        // given
        every { userRepository.findUserByEmailAndPassword("test@gmail.com", "INVALID") } returns null

        // when
        val exception2 = catchThrowable { classToTest.getNewCustomerSession("test@gmail.com", "INVALID") }

        // then
        verify(exactly = 1) { userRepository.findUserByEmailAndPassword("test@gmail.com", "INVALID") }
        verify(exactly = 0) { clientDtoConverter.convert(any()) }
        verify(exactly = 0) { userSessionRepository.saveAndFlush(any()) }
        then(exception2)
            .isInstanceOf(CustomerUsernameOrPasswordException::class.java)
            .hasMessageContaining("Username or password invalid")

    }

    // region Parameters

    companion object {
        @JvmStatic
        fun userRegisterMandatoryParameters(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("loginInfo"),
            )
        }
    }

    // endregion

}