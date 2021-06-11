package com.coach.flame.customer.client

import com.coach.flame.customer.EnrollmentProcessException
import com.coach.flame.domain.ClientStatusDto
import com.coach.flame.domain.maker.ClientDtoBuilder
import com.coach.flame.domain.maker.ClientDtoMaker
import com.natpryce.makeiteasy.MakeItEasy.with
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class ClientEnrollmentProcessImplTest {

    @MockK(relaxed = true)
    private lateinit var clientService: ClientService

    @InjectMockKs
    private lateinit var classToTest: ClientEnrollmentProcessImpl

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test init the enrollment process for a client`() {

        val uuidCoach = UUID.randomUUID()
        val clientDto = ClientDtoBuilder.makerWithLoginInfo().make()

        classToTest.init(clientDto, uuidCoach)

        verify { clientService.updateClientStatus(clientDto.identifier, ClientStatusDto.PENDING) }
        verify { clientService.linkCoach(clientDto.identifier, uuidCoach) }

    }

    @Test
    fun `test init the enrollment process for a client but client already has a coach`() {

        val uuidCoach = UUID.randomUUID()
        val clientDto = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.ACCEPTED))
            .make()

        val result = catchThrowable { classToTest.init(clientDto, uuidCoach) }

        // then
        verify(exactly = 0) { clientService.updateClientStatus(any(), any()) }
        verify(exactly = 0) { clientService.linkCoach(any(), any()) }
        then(result)
            .isInstanceOf(EnrollmentProcessException::class.java)
            .hasMessageContaining("Client already has a coach assigned.")

    }


    @Test
    fun `test finish the enrollment process for a client`() {

        val clientDto = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.PENDING))
            .make()

        classToTest.finish(clientDto, true)

        verify { clientService.updateClientStatus(clientDto.identifier, ClientStatusDto.ACCEPTED) }
        verify(exactly = 0) { clientService.unlinkCoach(clientDto.identifier) }

    }

    @Test
    fun `test finish the enrollment process for a client until call the init process`() {

        val clientDto = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.AVAILABLE))
            .make()

        val result = catchThrowable { classToTest.finish(clientDto, true) }

        // then
        verify(exactly = 0) { clientService.updateClientStatus(any(), any()) }
        verify(exactly = 0) { clientService.unlinkCoach(clientDto.identifier) }
        then(result)
            .isInstanceOf(EnrollmentProcessException::class.java)
            .hasMessageContaining("Client didn't start the enrollment process or already has a coach assigned.")

    }

    @Test
    fun `test finish the enrollment process for a client when client denial coach`() {

        val clientDto = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.PENDING))
            .make()

        classToTest.finish(clientDto, false)

        verify(exactly = 0) { clientService.updateClientStatus(clientDto.identifier, ClientStatusDto.ACCEPTED) }
        verify(exactly = 1) { clientService.unlinkCoach(clientDto.identifier) }

    }

    @Test
    fun `test break the enrollment between client and coach`() {

        val clientDto = ClientDtoBuilder.makerWithLoginInfo()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.ACCEPTED))
            .make()

        every { clientService.unlinkCoach(clientDto.identifier) } returns clientDto

        classToTest.`break`(clientDto)

        verify(exactly = 1) { clientService.unlinkCoach(clientDto.identifier) }

    }

}
