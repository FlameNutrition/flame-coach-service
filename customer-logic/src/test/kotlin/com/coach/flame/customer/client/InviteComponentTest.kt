package com.coach.flame.customer.client

import com.coach.flame.customer.register.RegistrationCustomerService
import com.coach.flame.domain.ClientStatusDto
import com.coach.flame.domain.maker.CoachDtoBuilder
import com.coach.flame.domain.maker.RegistrationInviteDtoBuilder
import com.coach.flame.domain.maker.RegistrationInviteDtoMaker
import com.coach.flame.jpa.entity.maker.ClientBuilder
import com.coach.flame.jpa.repository.ClientRepository
import com.natpryce.makeiteasy.MakeItEasy.with
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class InviteComponentTest {

    @MockK
    private lateinit var clientEnrollmentProcess: ClientEnrollmentProcess

    @MockK
    private lateinit var registrationCustomerService: RegistrationCustomerService

    @MockK
    private lateinit var clientRepository: ClientRepository

    @InjectMockKs
    private lateinit var classToTest: InviteComponent

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test send invite for client registration completed`() {

        val coachDto = CoachDtoBuilder.makerWithLoginInfo().make()
        val client = ClientBuilder.default()

        every { clientRepository.findByUserEmailIs("test@gmail.com") } returns client
        every {
            clientEnrollmentProcess.init(client.toDto(), coachDto.identifier)
        } answers {
            client.toDto()
                .copy(clientStatus = ClientStatusDto.PENDING)
        }

        val result = classToTest.send(coachDto, "test@gmail.com")

        verify(exactly = 0) { registrationCustomerService.sendRegistrationLink(any(), any()) }

        then(result.clientStatus).isEqualTo(ClientStatusDto.PENDING)
        then(result.isRegistrationInvite).isFalse
        then(result.sender).isEqualTo(coachDto.identifier)
    }

    @Test
    fun `test send invite for client without registration completed`() {

        val coachDto = CoachDtoBuilder.makerWithLoginInfo().make()
        val registrationInvite = RegistrationInviteDtoBuilder.maker()
            .but(with(RegistrationInviteDtoMaker.sender, coachDto))
            .make()

        every { clientRepository.findByUserEmailIs("test@gmail.com") } returns null
        every {
            registrationCustomerService.sendRegistrationLink(coachDto, "test@gmail.com")
        } answers {
            registrationInvite
        }

        val result = classToTest.send(coachDto, "test@gmail.com")

        verify(exactly = 0) { clientEnrollmentProcess.init(any(), any()) }

        then(result.clientStatus).isNull()
        then(result.isRegistrationInvite).isTrue
        then(result.sender).isEqualTo(coachDto.identifier)
        then(result.registrationLink).isEqualTo(registrationInvite.registrationLink)
        then(result.registrationKey).isEqualTo(registrationInvite.registrationKey)
    }

}
