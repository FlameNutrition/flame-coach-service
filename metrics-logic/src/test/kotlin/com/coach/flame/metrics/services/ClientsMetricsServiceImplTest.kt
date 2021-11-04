package com.coach.flame.metrics.services

import com.coach.flame.domain.ClientStatusDto
import com.coach.flame.domain.maker.ClientDtoBuilder
import com.coach.flame.domain.maker.ClientDtoMaker
import com.coach.flame.jpa.entity.Client.Companion.toClient
import com.coach.flame.jpa.entity.maker.CoachBuilder
import com.coach.flame.jpa.entity.maker.CoachMaker
import com.coach.flame.jpa.repository.operations.CoachRepositoryOperation
import com.coach.flame.metrics.MetricsFilter
import com.natpryce.makeiteasy.MakeItEasy.with
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class ClientsMetricsServiceImplTest {

    @MockK(relaxed = true)
    private lateinit var coachRepositoryOperation: CoachRepositoryOperation

    @InjectMockKs
    private lateinit var classToTest: ClientsMetricsServiceImpl

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test get clients metrics`() {

        val uuid = UUID.randomUUID()

        every {
            coachRepositoryOperation.getCoach(uuid)
        } returns CoachBuilder.maker()
            .but(
                with(
                    CoachMaker.clients, mutableListOf(
                        ClientDtoBuilder.makerWithLoginInfo()
                            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.PENDING))
                            .make()
                            .toClient(),
                        ClientDtoBuilder.makerWithLoginInfo()
                            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.PENDING))
                            .make()
                            .toClient(),
                        ClientDtoBuilder.makerWithLoginInfo()
                            .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.ACCEPTED))
                            .make()
                            .toClient()
                    )
                )
            )
            .make()

        val filter = MetricsFilter(uuid)

        val result = classToTest.getMetrics(filter)

        then(result.clients?.numberOfClientsAccepted).isEqualTo(1)
        then(result.clients?.numberOfClientsPending).isEqualTo(2)
        then(result.clients?.numberOfTotalClients).isEqualTo(3)

    }

}
