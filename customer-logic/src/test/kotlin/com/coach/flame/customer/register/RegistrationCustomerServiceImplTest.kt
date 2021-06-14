package com.coach.flame.customer.register

import com.coach.flame.base64.Base64
import com.coach.flame.customer.CustomerRegisterExpirationDate
import com.coach.flame.customer.CustomerRegisterInvalidEmail
import com.coach.flame.customer.CustomerRegisterWrongRegistrationKey
import com.coach.flame.customer.email.EmailService
import com.coach.flame.customer.props.PropsApplication
import com.coach.flame.date.DateHelper
import com.coach.flame.domain.maker.*
import com.coach.flame.jpa.entity.RegistrationInvite
import com.coach.flame.jpa.entity.maker.RegistrationInviteBuilder
import com.coach.flame.jpa.repository.RegistrationInviteRepository
import com.natpryce.makeiteasy.MakeItEasy.with
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class RegistrationCustomerServiceImplTest {

    @MockK
    private lateinit var registrationInviteRepository: RegistrationInviteRepository

    @MockK
    private lateinit var emailService: EmailService

    @MockK
    private lateinit var propsApplication: PropsApplication

    @InjectMockKs
    private lateinit var classToTest: RegistrationCustomerServiceImpl

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test send registration link and save the record into database`() {

        val coachDto = CoachDtoBuilder.makerWithLoginInfo().make()
        val entity = slot<RegistrationInvite>()
        val message = slot<String>()

        every { propsApplication.registrationLink } returns "http://localhost:8080/api"

        every {
            emailService.sendEmail(coachDto.loginInfo?.username, "client@test.com",
                "Flame Coach Registration Link",
                capture(message))
        } returns mockk()

        every {
            registrationInviteRepository.save(capture(entity))
        } answers {
            entity.captured
        }

        val result = classToTest.sendRegistrationLink(coachDto, "client@test.com")

        then(result.sender).isEqualTo(coachDto)
        then(result.sendTo).isEqualTo("client@test.com")
        then(result.registrationKey).isNotNull
        then(result.registrationLink).contains(result.registrationKey)
        then(result.registrationLink).startsWith("http://localhost:8080/api?registrationKey=")
        then(result.sendDttm).isNotNull
        then(result.acceptedDttm).isNull()

        then(message.captured).contains(listOf(
            String.format("Hello, " +
                    "Your coach %s, would like to invite you for the Flame Coach. This is a platform will allow you and " +
                    "your coach to track our progress. Good luck for this adventure.", coachDto.firstName),
            "Please use the following link to create our account:",
            "http://localhost:8080/api?registrationKey=",
            "&email=client@test.com"
        ))

    }

    @Test
    fun `test verify valid registration key`() {

        val date = DateHelper.toUTCDate(LocalDateTime.now().plusHours(2))
        val key = Base64.encode("${DateHelper.toISODate(date)}_test@gmail.com")

        val clientDto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.registrationKey, key),
                with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.maker()
                    .but(with(LoginInfoDtoMaker.username, "test@gmail.com"))
                    .make()))
            .make()

        every {
            registrationInviteRepository.existsByRegistrationKeyIs(key)
        } returns true

        val result = classToTest.checkRegistrationLink(clientDto)

        then(result).isTrue

    }

    @Test
    fun `test verify wrong registration key`() {

        val date = DateHelper.toUTCDate(LocalDateTime.now().minusHours(2))
        val key = Base64.encode("${DateHelper.toISODate(date)}_test@gmail.com")

        val clientDto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.registrationKey, key),
                with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.maker()
                    .but(with(LoginInfoDtoMaker.username, "test@gmail.com"))
                    .make()))
            .make()

        every {
            registrationInviteRepository.existsByRegistrationKeyIs(key)
        } returns false

        val result = catchThrowable { classToTest.checkRegistrationLink(clientDto) }

        then(result)
            .isInstanceOf(CustomerRegisterWrongRegistrationKey::class.java)
            .hasMessageContaining("Registration key invalid")

    }

    @Test
    fun `test verify expired registration key`() {

        val date = DateHelper.toUTCDate(LocalDateTime.now().minusSeconds(1))
        val key = Base64.encode("${DateHelper.toISODate(date)}_test@gmail.com")

        val clientDto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.registrationKey, key),
                with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.maker()
                    .but(with(LoginInfoDtoMaker.username, "test@gmail.com"))
                    .make()))
            .make()

        every {
            registrationInviteRepository.existsByRegistrationKeyIs(key)
        } returns true

        val result = catchThrowable { classToTest.checkRegistrationLink(clientDto) }

        then(result)
            .isInstanceOf(CustomerRegisterExpirationDate::class.java)
            .hasMessageContaining("Registration key expired")

    }

    @Test
    fun `test verify registration key when missing required parameters`() {

        val result0 = catchThrowable { classToTest.checkRegistrationLink(ClientDtoBuilder.default()) }

        then(result0)
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("registrationKey can not be null")

        val result1 = catchThrowable {
            classToTest.updateRegistration(ClientDtoBuilder.maker()
                .but(with(ClientDtoMaker.registrationKey, "KEY"))
                .make())
        }

        then(result1)
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("loginInfo can not be null")

    }

    @Test
    fun `test update registration invite`() {

        val clientDto = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.registrationKey, "MjAyMS0wNi0wNFQxNjo0MzoxMi4zMDE4ODhfdGVzdEBnbWFpbC5jb20="))
            .make()
        val registrationInvite = slot<RegistrationInvite>()

        every { propsApplication.registrationLink } returns "http://localhost:8080/api"
        every {
            registrationInviteRepository.findByRegistrationKeyIs("MjAyMS0wNi0wNFQxNjo0MzoxMi4zMDE4ODhfdGVzdEBnbWFpbC5jb20=")
        } returns RegistrationInviteBuilder.default()

        every {
            registrationInviteRepository.save(capture(registrationInvite))
        } answers {
            registrationInvite.captured
        }

        val result = classToTest.updateRegistration(clientDto)

        then(result.acceptedDttm).isNotNull
        then(registrationInvite.captured.acceptedDttm).isNotNull

    }

    @Test
    fun `test update registration invite when missing required parameters`() {

        val result0 = catchThrowable { classToTest.updateRegistration(ClientDtoBuilder.default()) }

        then(result0)
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("registrationKey can not be null")

        val result1 = catchThrowable {
            classToTest.updateRegistration(ClientDtoBuilder.maker()
                .but(with(ClientDtoMaker.registrationKey, "KEY"))
                .make())
        }

        then(result1)
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("loginInfo can not be null")

    }

    @Test
    fun `test verify registration key when email is wrong`() {

        val date = DateHelper.toUTCDate(LocalDateTime.now().plusHours(1))
        val key = Base64.encode("${DateHelper.toISODate(date)}_test@gmail.com")

        val clientDto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.registrationKey, key),
                with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.maker()
                    .but(with(LoginInfoDtoMaker.username, "invalid@gmail.com"))
                    .make()))
            .make()

        every {
            registrationInviteRepository.existsByRegistrationKeyIs(key)
        } returns true

        val result = catchThrowable { classToTest.checkRegistrationLink(clientDto) }

        then(result)
            .isInstanceOf(CustomerRegisterInvalidEmail::class.java)
            .hasMessageContaining("Invalid email, use the email received the registration link")

    }

}
