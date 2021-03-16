package com.coach.flame.customer.client

import com.coach.flame.domain.ClientDtoBuilder
import com.coach.flame.domain.ClientDtoMaker
import com.coach.flame.domain.ClientStatusDto
import com.natpryce.makeiteasy.MakeItEasy.with
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class ClientEnrollmentProcessImplTest {

    @MockK
    private lateinit var clientService: ClientService

    @InjectMockKs
    private lateinit var classToTest: ClientEnrollmentProcessImpl

    @Test
    fun `test init the enrollment process for a client`() {

        val uuidClient = UUID.randomUUID()
        val uuidCoach = UUID.randomUUID()
        val clientDto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.PENDING))
            .make()

        every { clientService.updateClientStatus(uuidClient, ClientStatusDto.PENDING) } returns clientDto
        every { clientService.updateClientCoach(uuidClient, uuidCoach) } returns clientDto

        val result = classToTest.init(uuidClient, uuidCoach)

        then(result.clientStatus).isEqualTo(ClientStatusDto.PENDING)

    }

    @Test
    fun `test finish the enrollment process for a client`() {

        val uuidClient = UUID.randomUUID()
        val clientDto = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.ACCEPTED))
            .make()

        every { clientService.updateClientStatus(uuidClient, ClientStatusDto.ACCEPTED) } returns clientDto

        val result = classToTest.finish(uuidClient)

        then(result.clientStatus).isEqualTo(ClientStatusDto.ACCEPTED)

    }

}