package com.coach.flame.customer.client

import com.coach.flame.customer.CustomerService
import com.coach.flame.customer.EnrollmentProcessException
import com.coach.flame.domain.*
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

    @MockK
    private lateinit var clientService: ClientService

    @MockK
    private lateinit var customerService: CustomerService

    @InjectMockKs
    private lateinit var classToTest: ClientEnrollmentProcessImpl

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test init the enrollment process for a client`() {

        val uuidClient = UUID.randomUUID()
        val uuidCoach = UUID.randomUUID()
        val clientDto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.PENDING))
            .make()

        every {
            customerService.getCustomer(uuidClient,
                CustomerTypeDto.CLIENT)
        } returns clientDto.copy(clientStatus = ClientStatusDto.AVAILABLE)
        every { clientService.updateClientStatus(uuidClient, ClientStatusDto.PENDING) } returns clientDto
        every { clientService.linkCoach(uuidClient, uuidCoach) } returns clientDto

        val result = classToTest.init(uuidClient, uuidCoach)

        then(result.clientStatus).isEqualTo(ClientStatusDto.PENDING)

    }

    @Test
    fun `test init the enrollment process for a client but client already has a coach`() {

        val uuidClient = UUID.randomUUID()
        val uuidCoach = UUID.randomUUID()
        val clientDto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.PENDING))
            .make()

        every { customerService.getCustomer(uuidClient, CustomerTypeDto.CLIENT) } returns clientDto

        val result = catchThrowable { classToTest.init(uuidClient, uuidCoach) }

        // then
        verify(exactly = 0) { clientService.updateClientStatus(any(), any()) }
        verify(exactly = 0) { clientService.linkCoach(any(), any()) }
        then(result)
            .isInstanceOf(EnrollmentProcessException::class.java)
            .hasMessageContaining("Client already has a coach assigned.")

    }


    @Test
    fun `test finish the enrollment process for a client`() {

        val uuidClient = UUID.randomUUID()
        val clientDto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.ACCEPTED))
            .make()

        every {
            customerService.getCustomer(uuidClient,
                CustomerTypeDto.CLIENT)
        } returns clientDto.copy(clientStatus = ClientStatusDto.PENDING)
        every { clientService.updateClientStatus(uuidClient, ClientStatusDto.ACCEPTED) } returns clientDto

        val result = classToTest.finish(uuidClient, true)

        then(result.clientStatus).isEqualTo(ClientStatusDto.ACCEPTED)

    }

    @Test
    fun `test finish the enrollment process for a client until call the init process`() {

        val uuidClient = UUID.randomUUID()
        val clientDto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.AVAILABLE))
            .make()

        every { customerService.getCustomer(uuidClient, CustomerTypeDto.CLIENT) } returns clientDto

        val result = catchThrowable { classToTest.finish(uuidClient, true) }

        // then
        verify(exactly = 0) { clientService.updateClientStatus(any(), any()) }
        then(result)
            .isInstanceOf(EnrollmentProcessException::class.java)
            .hasMessageContaining("Client didn't start the enrollment process or already has a coach assigned.")

    }

    @Test
    fun `test finish the enrollment process for a client when client denial coach`() {

        val uuidClient = UUID.randomUUID()
        val clientDto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.PENDING),
                with(ClientDtoMaker.coach, null as CoachDto?))
            .make()

        every { clientService.unlinkCoach(uuidClient) } returns clientDto

        val result = classToTest.finish(uuidClient, false)

        verify(exactly = 0) { clientService.updateClientStatus(any(), any()) }
        then(result.clientStatus).isEqualTo(ClientStatusDto.PENDING)
        then(result.coach).isNull()

    }

    @Test
    fun `test break the enrollment between client and coach`() {

        val uuidClient = UUID.randomUUID()
        val clientDto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.AVAILABLE),
                with(ClientDtoMaker.coach, null as CoachDto?))
            .make()

        every { clientService.unlinkCoach(uuidClient) } returns clientDto

        val result = classToTest.`break`(uuidClient)

        then(result.clientStatus).isEqualTo(ClientStatusDto.AVAILABLE)
        then(result.coach).isNull()

    }

}