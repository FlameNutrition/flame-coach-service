package com.coach.flame.api.metrics

import com.coach.flame.domain.MetricsDto
import com.coach.flame.metrics.MetricsService
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class MetricsImplTest {

    @MockK
    private lateinit var metricsService: MetricsService

    @InjectMockKs
    private lateinit var metricsImpl: MetricsImpl

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test get client statistics`() {

        val coachIdentifier = UUID.randomUUID()

        every { metricsService.getMetrics(coachIdentifier) } returns
                MetricsDto(clients = MetricsDto.Clients(
                    numberOfTotalClients = 20,
                    numberOfClientsPending = 9,
                    numberOfClientsAccepted = 11
                ))

        val response = metricsImpl.getClientsStatistics(coachIdentifier)

        then(response.coachIdentifier).isEqualTo(coachIdentifier)
        then(response.clientsStatus.numberOfClientsAccepted).isEqualTo(11)
        then(response.clientsStatus.numberOfClientsPending).isEqualTo(9)
        then(response.clientsStatus.numberOfTotalClients).isEqualTo(20)

    }

}
