package com.coach.flame.metrics

import com.coach.flame.domain.MetricsDto
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
class MetricsServiceTest {

    @MockK(relaxed = true)
    private lateinit var clientsMetricsService: ClientsMetricsService

    @InjectMockKs
    private lateinit var classToTest: MetricsService

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test get clients metrics`() {

        val uuid = UUID.randomUUID()

        every {
            clientsMetricsService.getClientsMetrics(uuid)
        } returns MetricsDto.Clients(
            numberOfTotalClients = 11,
            numberOfClientsAccepted = 5,
            numberOfClientsPending = 6
        )

        val result = classToTest.getMetrics(uuid)

        then(result.clients.numberOfTotalClients).isEqualTo(11)
        then(result.clients.numberOfClientsPending).isEqualTo(6)
        then(result.clients.numberOfClientsAccepted).isEqualTo(5)

    }

}
