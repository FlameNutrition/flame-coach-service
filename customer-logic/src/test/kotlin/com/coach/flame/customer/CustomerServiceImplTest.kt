package com.coach.flame.customer

import com.coach.flame.customer.security.HashPassword
import com.coach.flame.customer.security.Salt
import com.coach.flame.domain.*
import com.coach.flame.domain.converters.ClientDtoConverter
import com.coach.flame.domain.converters.CoachDtoConverter
import com.coach.flame.domain.converters.CountryDtoConverter
import com.coach.flame.domain.converters.GenderDtoConverter
import com.coach.flame.jpa.entity.*
import com.coach.flame.jpa.repository.*
import com.coach.flame.jpa.repository.cache.ConfigCache
import com.natpryce.makeiteasy.MakeItEasy.*
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.dao.DataIntegrityViolationException
import java.time.LocalDate
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

    @MockK
    private lateinit var countryConfigCache: ConfigCache<CountryConfig>

    @MockK
    private lateinit var genderConfigCache: ConfigCache<GenderConfig>

    @MockK
    private lateinit var hashPasswordTool: HashPassword

    @MockK
    private lateinit var saltTool: Salt

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

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `get valid client`() {

        // given
        val uuid = UUID.randomUUID()
        val client = ClientBuilder.default()
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
        val coach = CoachBuilder.default()
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
        val preClientDto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.maker()
                .but(with(LoginInfoDtoMaker.expirationDate, expectedExpirationDate),
                    with(LoginInfoDtoMaker.token, expectedToken),
                    with(LoginInfoDtoMaker.password, "HASH_PASSWORD"),
                    with(LoginInfoDtoMaker.keyDecrypt, "MY_SALT"))
                .make()))
            .make()

        val clientCaptorSlot = slot<Client>()
        every { saltTool.generate() } returns "MY_SALT"
        every { hashPasswordTool.generate(preClientDto.loginInfo!!.password, "MY_SALT") } returns "HASH_PASSWORD"
        every { clientTypeRepository.getByType("CLIENT") } returns ClientTypeBuilder.default()
        every { clientRepository.saveAndFlush(capture(clientCaptorSlot)) } answers { clientCaptorSlot.captured }

        // when
        val postClientDto = classToTest.registerCustomer(preClientDto) as ClientDto

        // then
        verify(exactly = 1) { clientDtoConverter.convert(any()) }
        verify(exactly = 1) { clientRepository.saveAndFlush(any()) }
        verify(exactly = 0) { coachRepository.saveAndFlush(any()) }
        then(clientCaptorSlot.isCaptured).isTrue
        then(postClientDto.loginInfo).isNotNull
        then(postClientDto.firstName).isEqualTo(clientCaptorSlot.captured.firstName)
        then(postClientDto.lastName).isEqualTo(clientCaptorSlot.captured.lastName)
        then(postClientDto.customerType).isEqualTo(CustomerTypeDto.CLIENT)
        then(postClientDto.loginInfo!!.username).isEqualTo(clientCaptorSlot.captured.user.email)
        then(postClientDto.loginInfo!!.password).isEqualTo(clientCaptorSlot.captured.user.password)
        then(postClientDto.loginInfo!!.keyDecrypt).isEqualTo(clientCaptorSlot.captured.user.keyDecrypt)
        then(postClientDto.loginInfo!!.expirationDate).isEqualTo(clientCaptorSlot.captured.user.userSession.expirationDate)
        then(postClientDto.loginInfo!!.token).isEqualTo(clientCaptorSlot.captured.user.userSession.token)
    }

    @Test
    fun `register a new coach`() {

        // given
        val expectedExpirationDate = LocalDateTime.now()
        val expectedToken = UUID.randomUUID()
        val preClientDto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.customerType, CustomerTypeDto.COACH),
                with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.maker()
                    .but(with(LoginInfoDtoMaker.expirationDate, expectedExpirationDate),
                        with(LoginInfoDtoMaker.token, expectedToken),
                        with(LoginInfoDtoMaker.password, "HASH_PASSWORD"),
                        with(LoginInfoDtoMaker.keyDecrypt, "MY_SALT"))
                    .make()))
            .make()

        val clientCaptorSlot = slot<Coach>()
        every { saltTool.generate() } returns "MY_SALT"
        every { hashPasswordTool.generate(preClientDto.loginInfo!!.password, "MY_SALT") } returns "HASH_PASSWORD"
        every { clientTypeRepository.getByType("COACH") } returns ClientTypeBuilder.maker()
            .but(with(ClientTypeMaker.type, "COACH"))
            .make()
        every { coachRepository.saveAndFlush(capture(clientCaptorSlot)) } answers { clientCaptorSlot.captured }

        // when
        val postClientDto = classToTest.registerCustomer(preClientDto) as CoachDto

        // then
        verify(exactly = 1) { coachDtoConverter.convert(any()) }
        verify(exactly = 0) { clientRepository.saveAndFlush(any()) }
        verify(exactly = 1) { coachRepository.saveAndFlush(any()) }
        then(clientCaptorSlot.isCaptured).isTrue
        then(postClientDto.loginInfo).isNotNull
        then(postClientDto.firstName).isEqualTo(clientCaptorSlot.captured.firstName)
        then(postClientDto.lastName).isEqualTo(clientCaptorSlot.captured.lastName)
        then(postClientDto.customerType).isEqualTo(CustomerTypeDto.COACH)
        then(postClientDto.loginInfo!!.username).isEqualTo(clientCaptorSlot.captured.user.email)
        then(postClientDto.loginInfo!!.password).isEqualTo(clientCaptorSlot.captured.user.password)
        then(postClientDto.loginInfo!!.keyDecrypt).isEqualTo(clientCaptorSlot.captured.user.keyDecrypt)
        then(postClientDto.loginInfo!!.expirationDate).isEqualTo(clientCaptorSlot.captured.user.userSession.expirationDate)
        then(postClientDto.loginInfo!!.token).isEqualTo(clientCaptorSlot.captured.user.userSession.token)
    }


    @Test
    fun `register a new client duplicated`() {

        // given
        val entityClient = ClientBuilder.default()
        val clientDto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.default()))
            .make()
        every { saltTool.generate() } returns "MY_SALT"
        every { hashPasswordTool.generate(clientDto.loginInfo!!.password, "MY_SALT") } returns "HASH_PASSWORD"
        every { clientTypeRepository.getByType(any()) } returns entityClient.clientType
        every { clientRepository.saveAndFlush(any()) } throws DataIntegrityViolationException("SQL ERROR!")

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
        val entityClient = ClientBuilder.default()
        val clientDto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.default()))
            .make()
        every { saltTool.generate() } returns "MY_SALT"
        every { hashPasswordTool.generate(clientDto.loginInfo!!.password, "MY_SALT") } returns "HASH_PASSWORD"
        every { clientTypeRepository.getByType(any()) } returns entityClient.clientType
        every { clientRepository.saveAndFlush(any()) } throws RuntimeException("Something wrong happened!")

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
                expectedMessage = "loginInfo is a mandatory parameter"
                ClientDtoBuilder.maker()
                    .but(with(ClientDtoMaker.loginInfo, null as LoginInfoDto?))
                    .make()
            }
            else -> ClientDtoBuilder.default()
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
        val entityClient = ClientBuilder.maker()
            .but(with(ClientMaker.user,
                make(a(UserMaker.User,
                    with(UserMaker.email, "test@gmail.com"),
                    with(UserMaker.password, "HASHING_PASSWORD")))))
            .make()
        val user = UserBuilder.maker()
            .but(with(UserMaker.email, "test@gmail.com"),
                with(UserMaker.password, "HASHING_PASSWORD"),
                with(UserMaker.client, entityClient))
            .make()

        every { hashPasswordTool.generate("HASHING_PASSWORD",  "salt") } returns "HASHING_PASSWORD"
        every { hashPasswordTool.verify("12345", "HASHING_PASSWORD", "salt") } returns true
        every { userRepository.findUserByEmail("test@gmail.com") } returns user
        every { userSessionRepository.save(capture(userSession)) } returns mockk()

        // when
        val response = classToTest.getNewCustomerSession(username, password) as ClientDto

        // then
        verify(exactly = 1) { userRepository.findUserByEmail("test@gmail.com") }
        verify(exactly = 1) { userSessionRepository.save(any()) }
        verify(exactly = 1) { clientDtoConverter.convert(any()) }
        then(userSession.isCaptured).isTrue
        then(userSession.captured.token).isNotNull
        then(userSession.captured.expirationDate).isNotEqualTo(actualDate)
        then(response.firstName).isNotEmpty
        then(response.lastName).isNotEmpty
        then(response.loginInfo?.username).isEqualTo("test@gmail.com")
        then(response.loginInfo?.password).isEqualTo("HASHING_PASSWORD")
        then(response.loginInfo?.keyDecrypt).isEqualTo("salt")
        then(response.loginInfo?.expirationDate).isEqualTo(userSession.captured.expirationDate)
        then(response.loginInfo?.token).isEqualTo(userSession.captured.token)

    }

    @Test
    fun `get a new client session but username or password is invalid`() {

        // given
        every { userRepository.findUserByEmail("INVALID") } returns null

        // when
        val exception1 = catchThrowable { classToTest.getNewCustomerSession("INVALID", "12345") }

        // then
        verify(exactly = 1) { userRepository.findUserByEmail("INVALID") }
        verify(exactly = 0) { clientDtoConverter.convert(any()) }
        verify(exactly = 0) { userSessionRepository.saveAndFlush(any()) }
        then(exception1)
            .isInstanceOf(CustomerUsernameOrPasswordException::class.java)
            .hasMessageContaining("Username invalid")

        // given
        every { userRepository.findUserByEmail("test@gmail.com") } returns null

        // when
        val exception2 = catchThrowable { classToTest.getNewCustomerSession("test@gmail.com", "INVALID") }

        // then
        verify(exactly = 1) { userRepository.findUserByEmail("test@gmail.com") }
        verify(exactly = 0) { clientDtoConverter.convert(any()) }
        verify(exactly = 0) { userSessionRepository.saveAndFlush(any()) }
        then(exception2)
            .isInstanceOf(CustomerUsernameOrPasswordException::class.java)
            .hasMessageContaining("Username invalid")

    }

    @Test
    fun `get a new coach session`() {

        // given
        val username = "test@gmail.com"
        val password = "12345"
        val userSession = slot<UserSession>()
        val actualDate = LocalDateTime.now()
        val entityUserSession = UserSessionBuilder.maker()
            .but(with(UserSessionMaker.expirationDate, actualDate))
            .make()

        val entityCoach = CoachBuilder.maker()
            .but(with(CoachMaker.user, UserBuilder.maker()
                .but(with(UserMaker.email, "test@gmail.com"), with(UserMaker.password, "HASHING_PASSWORD"))
                .make()),
                with(CoachMaker.userSession, entityUserSession))
            .make()
        val user = UserBuilder.maker()
            .but(with(UserMaker.email, "test@gmail.com"),
                with(UserMaker.password, "HASHING_PASSWORD"),
                with(UserMaker.coach, entityCoach))
            .make()

        every { hashPasswordTool.generate("HASHING_PASSWORD",  "salt") } returns "HASHING_PASSWORD"
        every { hashPasswordTool.verify("12345", "HASHING_PASSWORD", "salt") } returns true
        every { userRepository.findUserByEmail("test@gmail.com") } returns user
        every { userSessionRepository.save(capture(userSession)) } returns mockk()

        // when
        val response = classToTest.getNewCustomerSession(username, password) as CoachDto

        // then
        verify(exactly = 1) { userRepository.findUserByEmail("test@gmail.com") }
        verify(exactly = 1) { userSessionRepository.save(any()) }
        verify(exactly = 1) { coachDtoConverter.convert(any()) }
        then(userSession.isCaptured).isTrue
        then(userSession.captured.token).isNotNull
        then(userSession.captured.expirationDate).isNotEqualTo(actualDate)
        then(response.firstName).isNotEmpty
        then(response.lastName).isNotEmpty
        then(response.loginInfo?.username).isEqualTo("test@gmail.com")
        then(response.loginInfo?.password).isEqualTo("HASHING_PASSWORD")
        then(response.loginInfo?.keyDecrypt).isEqualTo("salt")
        then(response.loginInfo?.expirationDate).isEqualTo(userSession.captured.expirationDate)
        then(response.loginInfo?.token).isEqualTo(userSession.captured.token)

    }

    @Test
    fun `update client customer`() {

        val identifier = UUID.randomUUID()
        val countryDto = CountryDtoBuilder.maker()
            .but(with(CountryDtoMaker.countryCode, "PT"),
                with(CountryDtoMaker.externalValue, "Portugal"))
            .make()
        val genderDto = GenderDtoBuilder.maker()
            .but(with(GenderDtoMaker.genderCode, "M"),
                with(GenderDtoMaker.externalValue, "Male"))
            .make()
        val entity = slot<Client>()
        val client = ClientBuilder.maker()
            .but(with(ClientMaker.user, UserBuilder.default()),
                with(ClientMaker.clientStatus, ClientStatus.ACCEPTED),
                with(ClientMaker.coach, CoachBuilder.default()))
            .make()
        val clientDto = ClientDtoBuilder.maker()
            .but(
                with(ClientDtoMaker.firstName, "FIRSTNAME"),
                with(ClientDtoMaker.lastName, "LASTNAME"),
                with(ClientDtoMaker.birthday, LocalDate.parse("2021-04-04")),
                with(ClientDtoMaker.phoneCode, "+44"),
                with(ClientDtoMaker.phoneNumber, "22334455"),
                with(ClientDtoMaker.country, countryDto),
                with(ClientDtoMaker.gender, genderDto),
                with(ClientDtoMaker.weight, 70.5f),
                with(ClientDtoMaker.height, 1.70f),
                with(ClientDtoMaker.measureType, MeasureTypeDto.LBS_IN),
            ).make()

        every { clientRepository.findByUuid(identifier) } returns client
        every { countryConfigCache.getValue("PT") } returns Optional.of(CountryBuilder.maker()
            .but(with(CountryMaker.externalValue, "Portugal"),
                with(CountryMaker.countryCode, "PT"))
            .make())
        every { genderConfigCache.getValue("M") } returns Optional.of(GenderBuilder.maker()
            .but(with(GenderMaker.genderCode, "M"),
                with(GenderMaker.externalValue, "Male"))
            .make())
        every { clientRepository.save(capture(entity)) } answers { entity.captured }

        val response = classToTest.updateCustomer(identifier, clientDto) as ClientDto

        val entityCaptured = entity.captured

        then(entityCaptured.firstName).isEqualTo(clientDto.firstName)
        then(entityCaptured.lastName).isEqualTo(clientDto.lastName)
        then(entityCaptured.birthday).isEqualTo(clientDto.birthday)
        then(entityCaptured.phoneCode).isEqualTo(clientDto.phoneCode)
        then(entityCaptured.phoneNumber).isEqualTo(clientDto.phoneNumber)
        then(entityCaptured.country?.countryCode).isEqualTo(clientDto.country?.countryCode)
        then(entityCaptured.country?.externalValue).isEqualTo(clientDto.country?.externalValue)
        then(entityCaptured.gender?.genderCode).isEqualTo(clientDto.gender?.genderCode)
        then(entityCaptured.gender?.externalValue).isEqualTo(clientDto.gender?.externalValue)
        then(entityCaptured.weight).isEqualTo(clientDto.weight)
        then(entityCaptured.height).isEqualTo(clientDto.height)
        then(entityCaptured.measureConfig.code).isEqualTo(clientDto.measureType.code)

        // Attributes should not be updated
        then(response.identifier).isEqualTo(entityCaptured.uuid)
        then(response.customerType.name).isEqualTo(entityCaptured.clientType.type.toUpperCase())
        then(response.loginInfo?.username).isEqualTo(entityCaptured.user.email)
        then(response.loginInfo?.token).isEqualTo(entityCaptured.user.userSession.token)
        then(response.loginInfo?.expirationDate).isEqualTo(entityCaptured.user.userSession.expirationDate)
        then(response.registrationDate).isEqualTo(entityCaptured.registrationDate)
        then(response.clientStatus?.name).isEqualTo(entityCaptured.clientStatus.name)
        then(response.coach?.identifier).isEqualTo(entityCaptured.coach?.uuid)
    }

    @Test
    fun `update coach customer`() {

        val identifier = UUID.randomUUID()
        val countryDto = CountryDtoBuilder.maker()
            .but(with(CountryDtoMaker.countryCode, "PT"),
                with(CountryDtoMaker.externalValue, "Portugal"))
            .make()
        val genderDto = GenderDtoBuilder.maker()
            .but(with(GenderDtoMaker.genderCode, "M"),
                with(GenderDtoMaker.externalValue, "Male"))
            .make()
        val entity = slot<Coach>()
        val coach = CoachBuilder.maker()
            .but(with(CoachMaker.user, UserBuilder.default()))
            .make()
        val coachDto = CoachDtoBuilder.maker()
            .but(
                with(CoachDtoMaker.firstName, "FIRSTNAME"),
                with(CoachDtoMaker.lastName, "LASTNAME"),
                with(CoachDtoMaker.birthday, LocalDate.parse("2021-04-04")),
                with(CoachDtoMaker.phoneCode, "+44"),
                with(CoachDtoMaker.phoneNumber, "22334455"),
                with(CoachDtoMaker.country, countryDto),
                with(CoachDtoMaker.gender, genderDto),
            ).make()

        every { countryConfigCache.getValue("PT") } returns Optional.of(CountryBuilder.maker()
            .but(with(CountryMaker.externalValue, "Portugal"),
                with(CountryMaker.countryCode, "PT"))
            .make())
        every { genderConfigCache.getValue("M") } returns Optional.of(GenderBuilder.maker()
            .but(with(GenderMaker.genderCode, "M"),
                with(GenderMaker.externalValue, "Male"))
            .make())
        every { coachRepository.findByUuid(identifier) } returns coach
        every { coachRepository.save(capture(entity)) } answers { entity.captured }

        val response = classToTest.updateCustomer(identifier, coachDto) as CoachDto

        val entityCaptured = entity.captured

        then(entityCaptured.firstName).isEqualTo(coachDto.firstName)
        then(entityCaptured.lastName).isEqualTo(coachDto.lastName)
        then(entityCaptured.birthday).isEqualTo(coachDto.birthday)
        then(entityCaptured.phoneCode).isEqualTo(coachDto.phoneCode)
        then(entityCaptured.phoneNumber).isEqualTo(coachDto.phoneNumber)
        then(entityCaptured.country?.countryCode).isEqualTo(coachDto.country?.countryCode)
        then(entityCaptured.country?.externalValue).isEqualTo(coachDto.country?.externalValue)
        then(entityCaptured.gender?.genderCode).isEqualTo(coachDto.gender?.genderCode)
        then(entityCaptured.gender?.externalValue).isEqualTo(coachDto.gender?.externalValue)

        // Attributes should not be updated

        then(response.identifier).isEqualTo(entityCaptured.uuid)
        then(response.customerType.name).isEqualTo(entityCaptured.clientType.type.toUpperCase())
        then(response.loginInfo?.username).isEqualTo(entityCaptured.user.email)
        then(response.loginInfo?.token).isEqualTo(entityCaptured.user.userSession.token)
        then(response.loginInfo?.expirationDate).isEqualTo(entityCaptured.user.userSession.expirationDate)
        then(response.registrationDate).isEqualTo(entityCaptured.registrationDate)
    }

    // region Parameters

    companion object {
        @JvmStatic
        fun userRegisterMandatoryParameters(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("loginInfo")
            )
        }
    }

    // endregion

}
