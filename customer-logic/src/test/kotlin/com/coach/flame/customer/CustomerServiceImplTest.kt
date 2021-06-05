package com.coach.flame.customer

import com.coach.flame.customer.register.RegistrationCustomerService
import com.coach.flame.customer.security.HashPassword
import com.coach.flame.customer.security.Salt
import com.coach.flame.domain.*
import com.coach.flame.domain.maker.*
import com.coach.flame.jpa.entity.*
import com.coach.flame.jpa.entity.maker.*
import com.coach.flame.jpa.repository.*
import com.coach.flame.jpa.repository.cache.ConfigCache
import com.natpryce.makeiteasy.MakeItEasy.*
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
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
    private lateinit var registrationCustomerService: RegistrationCustomerService

    @MockK
    private lateinit var countryConfigCache: ConfigCache<CountryConfig>

    @MockK
    private lateinit var genderConfigCache: ConfigCache<GenderConfig>

    @MockK
    private lateinit var hashPasswordTool: HashPassword

    @MockK
    private lateinit var saltTool: Salt

    @InjectMockKs
    private lateinit var classToTest: CustomerServiceImpl

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `get valid client`() {

        // given
        val uuid1 = UUID.randomUUID()
        val uuid2 = UUID.randomUUID()
        val clientWithoutCoach = ClientBuilder.default()
        val clientWithCoach = ClientBuilder.maker()
            .but(with(ClientMaker.coach, CoachBuilder.default()))
            .make()

        every { clientRepository.findByUuid(uuid1) } returns clientWithoutCoach
        every { clientRepository.findByUuid(uuid2) } returns clientWithCoach

        // when
        val clientDtoWithoutCoach = classToTest.getCustomer(uuid1, CustomerTypeDto.CLIENT)
        val clientDtoWithCoach = classToTest.getCustomer(uuid2, CustomerTypeDto.CLIENT)

        // then
        verify(exactly = 1) { clientRepository.findByUuid(uuid1) }
        verify(exactly = 1) { clientRepository.findByUuid(uuid2) }
        then(clientDtoWithoutCoach).isNotNull
        then(clientDtoWithCoach).isNotNull
        then((clientDtoWithCoach as ClientDto).coach).isNotNull

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
        every { registrationCustomerService.checkRegistrationLink(any()) } returns true
        every { registrationCustomerService.updateRegistration(any()) } returns mockk()

        // when
        val postClientDto = classToTest.registerCustomer(preClientDto) as ClientDto

        // then
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
        every { registrationCustomerService.checkRegistrationLink(any()) } returns true

        // when
        val exception = catchThrowable { classToTest.registerCustomer(clientDto) }

        // then
        then(exception)
            .isInstanceOf(CustomerRegisterDuplicateException::class.java)
            .hasMessageContaining("The following customer already exists")

        verify(exactly = 0) { registrationCustomerService.updateRegistration(any()) }

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
        every { registrationCustomerService.checkRegistrationLink(any()) } returns true

        // when
        val exception = catchThrowable { classToTest.registerCustomer(clientDto) }

        // then
        verify(exactly = 0) { registrationCustomerService.updateRegistration(any()) }
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
        then(exception)
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining(expectedMessage)

    }

    @Test
    fun `get a new client session`() {

        // given
        val password = "12345"
        val userSession = slot<UserSession>()
        val actualDate = LocalDateTime.now()
        val entityClientWithoutCoach = ClientBuilder.maker()
            .but(with(ClientMaker.user,
                make(a(UserMaker.User,
                    with(UserMaker.email, "test@gmail.com"),
                    with(UserMaker.password, "HASHING_PASSWORD")))))
            .make()
        val entityClientWithCoach = ClientBuilder.maker()
            .but(with(ClientMaker.user, make(a(UserMaker.User,
                with(UserMaker.email, "test@gmail.com"),
                with(UserMaker.password, "HASHING_PASSWORD")))),
                with(ClientMaker.coach, CoachBuilder.default()))
            .make()
        val userWithoutCoach = UserBuilder.maker()
            .but(with(UserMaker.email, "test@gmail.com"),
                with(UserMaker.password, "HASHING_PASSWORD"),
                with(UserMaker.client, entityClientWithoutCoach))
            .make()
        val userWithCoach = UserBuilder.maker()
            .but(with(UserMaker.email, "test2@gmail.com"),
                with(UserMaker.password, "HASHING_PASSWORD"),
                with(UserMaker.client, entityClientWithCoach))
            .make()

        every { hashPasswordTool.generate("HASHING_PASSWORD", "salt") } returns "HASHING_PASSWORD"
        every { hashPasswordTool.verify("12345", "HASHING_PASSWORD", "salt") } returns true
        every { userRepository.findUserByEmail("test@gmail.com") } returns userWithoutCoach
        every { userRepository.findUserByEmail("test2@gmail.com") } returns userWithCoach
        every { userSessionRepository.save(capture(userSession)) } returns mockk()

        // when
        val responseWithoutCoach = classToTest.getNewCustomerSession("test@gmail.com", password) as ClientDto

        // then
        verify(exactly = 1) { userRepository.findUserByEmail("test@gmail.com") }
        verify(exactly = 1) { userSessionRepository.save(any()) }
        then(userSession.isCaptured).isTrue
        then(userSession.captured.token).isNotNull
        then(userSession.captured.expirationDate).isNotEqualTo(actualDate)
        then(responseWithoutCoach.firstName).isNotEmpty
        then(responseWithoutCoach.lastName).isNotEmpty
        then(responseWithoutCoach.coach).isNull()
        then(responseWithoutCoach.loginInfo?.username).isEqualTo("test@gmail.com")
        then(responseWithoutCoach.loginInfo?.password).isEqualTo("HASHING_PASSWORD")
        then(responseWithoutCoach.loginInfo?.keyDecrypt).isEqualTo("salt")
        then(responseWithoutCoach.loginInfo?.expirationDate).isEqualTo(userSession.captured.expirationDate)
        then(responseWithoutCoach.loginInfo?.token).isEqualTo(userSession.captured.token)

        val responseWithCoach = classToTest.getNewCustomerSession("test2@gmail.com", password) as ClientDto

        verify(exactly = 1) { userRepository.findUserByEmail("test2@gmail.com") }
        verify(exactly = 2) { userSessionRepository.save(any()) }
        then(responseWithCoach.coach).isNotNull
    }

    @Test
    fun `get a new client session but username or password is invalid`() {

        // given
        every { userRepository.findUserByEmail("INVALID") } returns null

        // when
        val exception1 = catchThrowable { classToTest.getNewCustomerSession("INVALID", "12345") }

        // then
        verify(exactly = 1) { userRepository.findUserByEmail("INVALID") }
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

        every { hashPasswordTool.generate("HASHING_PASSWORD", "salt") } returns "HASHING_PASSWORD"
        every { hashPasswordTool.verify("12345", "HASHING_PASSWORD", "salt") } returns true
        every { userRepository.findUserByEmail("test@gmail.com") } returns user
        every { userSessionRepository.save(capture(userSession)) } returns mockk()

        // when
        val response = classToTest.getNewCustomerSession(username, password) as CoachDto

        // then
        verify(exactly = 1) { userRepository.findUserByEmail("test@gmail.com") }
        verify(exactly = 1) { userSessionRepository.save(any()) }
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

        val identifier1 = UUID.randomUUID()
        val identifier2 = UUID.randomUUID()
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
        val clientWithoutCoach = ClientBuilder.maker()
            .but(with(ClientMaker.user, UserBuilder.default()),
                with(ClientMaker.clientStatus, ClientStatus.ACCEPTED))
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
        val clientDtoWithoutCoach = ClientDtoBuilder.default()

        every { clientRepository.findByUuid(identifier1) } returns client
        every { clientRepository.findByUuid(identifier2) } returns clientWithoutCoach
        every { countryConfigCache.getValue("PT") } returns Optional.of(CountryBuilder.maker()
            .but(with(CountryMaker.externalValue, "Portugal"),
                with(CountryMaker.countryCode, "PT"))
            .make())
        every { genderConfigCache.getValue("M") } returns Optional.of(GenderBuilder.maker()
            .but(with(GenderMaker.genderCode, "M"),
                with(GenderMaker.externalValue, "Male"))
            .make())
        every { clientRepository.save(capture(entity)) } answers { entity.captured }

        val response1 = classToTest.updateCustomer(identifier1, clientDto) as ClientDto

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
        then(response1.identifier).isEqualTo(entityCaptured.uuid)
        then(response1.customerType.name).isEqualTo(entityCaptured.clientType.type.toUpperCase())
        then(response1.loginInfo?.username).isEqualTo(entityCaptured.user.email)
        then(response1.loginInfo?.token).isEqualTo(entityCaptured.user.userSession.token)
        then(response1.loginInfo?.expirationDate).isEqualTo(entityCaptured.user.userSession.expirationDate)
        then(response1.registrationDate).isEqualTo(entityCaptured.registrationDate)
        then(response1.clientStatus?.name).isEqualTo(entityCaptured.clientStatus.name)
        then(response1.coach?.identifier).isEqualTo(entityCaptured.coach?.uuid)


        every { clientRepository.save(any()) } returns clientWithoutCoach
        val response2 = classToTest.updateCustomer(identifier2, clientDtoWithoutCoach) as ClientDto

        then(response2.coach).isNull()
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

    @Test
    fun `update password customer`() {

        val user = UserBuilder.maker()
            .but(with(UserMaker.password, "##55-332"),
                with(UserMaker.key, "MY_SALT"))
            .make()
        val entity = slot<User>()

        every { saltTool.generate() } returns "MY_SALT"
        every { hashPasswordTool.verify("OLD", "##55-332", "MY_SALT") } returns true
        every { hashPasswordTool.generate("##55-332", "MY_SALT") } returns "HASH_PASSWORD"
        every { hashPasswordTool.generate("NEW", "MY_SALT") } returns "HASH_PASSWORD_2"
        every { userRepository.findUserByEmail("test@gmail.com") } returns user
        every { userRepository.saveAndFlush(capture(entity)) } returns mockk()

        // when
        classToTest.updateCustomerPassword("test@gmail.com", "OLD", "NEW")

        // then
        verify(exactly = 1) { userRepository.saveAndFlush(any()) }
        verify(exactly = 1) { userRepository.findUserByEmail("test@gmail.com") }
        then(entity.captured.keyDecrypt).isEqualTo("MY_SALT")
        then(entity.captured.password).isEqualTo("HASH_PASSWORD_2")
    }

    @Test
    fun `update password customer but previous password is wrong`() {

        val user = UserBuilder.maker()
            .but(with(UserMaker.password, "##55-332"),
                with(UserMaker.key, "MY_SALT"))
            .make()

        every { userRepository.findUserByEmail("test@gmail.com") } returns user
        every { hashPasswordTool.verify("OLD", "##55-332", "MY_SALT") } returns false

        // when
        val exception = catchThrowable { classToTest.updateCustomerPassword("test@gmail.com", "OLD", "NEW") }

        // then
        verify(exactly = 1) { userRepository.findUserByEmail("test@gmail.com") }
        then(exception)
            .isInstanceOf(CustomerUsernameOrPasswordException::class.java)
            .hasMessageContaining("Password invalid")
    }

    @Test
    fun `test when customer type is invalid`() {

        // when
        val getCustomer = catchThrowable { classToTest.getCustomer(UUID.randomUUID(), CustomerTypeDto.UNKNOWN) }
        val updateCustomer = catchThrowable {
            classToTest.updateCustomer(UUID.randomUUID(), ClientDtoBuilder
                .maker()
                .but(with(ClientDtoMaker.customerType, CustomerTypeDto.UNKNOWN))
                .make())
        }

        // then
        then(getCustomer)
            .isInstanceOf(CustomerException::class.java)
            .hasMessageContaining("UNKNOWN is an invalid customer type")

        then(updateCustomer)
            .isInstanceOf(CustomerException::class.java)
            .hasMessageContaining("UNKNOWN is an invalid customer type")

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
