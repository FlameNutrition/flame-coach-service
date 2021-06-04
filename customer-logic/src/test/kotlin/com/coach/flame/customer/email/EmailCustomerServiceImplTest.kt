package com.coach.flame.customer.email

import com.coach.flame.customer.props.PropsApplication
import com.coach.flame.domain.maker.CoachDtoBuilder
import com.coach.flame.jpa.entity.RegistrationInvite
import com.coach.flame.jpa.repository.RegistrationInviteRepository
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class EmailCustomerServiceImplTest {

    @MockK
    private lateinit var registrationInviteRepository: RegistrationInviteRepository

    @MockK
    private lateinit var emailService: EmailService

    @MockK
    private lateinit var propsApplication: PropsApplication

    @InjectMockKs
    private lateinit var classToTest: EmailCustomerServiceImpl

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
                "Flame Coach registration link",
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

}
