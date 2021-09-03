package com.coach.flame.api.metrics

import com.coach.flame.date.DateHelper
import com.coach.flame.domain.DateIntervalDto
import com.coach.flame.domain.MetricsDto
import com.coach.flame.domain.metrics.Clients
import com.coach.flame.domain.metrics.Incomes
import com.coach.flame.metrics.MetricsFilter
import com.coach.flame.metrics.services.ClientsMetricsServiceImpl
import com.coach.flame.metrics.services.IncomeMetricsServiceImpl
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
class MetricsImplTest {

    @MockK
    private lateinit var clientsMetricsService: ClientsMetricsServiceImpl

    @MockK
    private lateinit var incomesMetricsService: IncomeMetricsServiceImpl

    @InjectMockKs
    private lateinit var metricsImpl: MetricsImpl

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test get client statistics`() {

        val coachIdentifier = UUID.randomUUID()

        every { clientsMetricsService.getMetrics(MetricsFilter(coachIdentifier)) } returns
                MetricsDto().apply {
                    clients = Clients(
                        numberOfTotalClients = 20,
                        numberOfClientsPending = 9,
                        numberOfClientsAccepted = 11
                    )
                }

        val response = metricsImpl.getClientsStatistics(coachIdentifier)

        then(response.identifier).isEqualTo(coachIdentifier)
        then(response.clientsStatus?.numberOfClientsAccepted).isEqualTo(11)
        then(response.clientsStatus?.numberOfClientsPending).isEqualTo(9)
        then(response.clientsStatus?.numberOfTotalClients).isEqualTo(20)

    }

    @Test
    fun `test get empty client statistics`() {

        val coachIdentifier = UUID.randomUUID()

        every { clientsMetricsService.getMetrics(MetricsFilter(coachIdentifier)) } returns MetricsDto()

        val response = metricsImpl.getClientsStatistics(coachIdentifier)

        then(response.identifier).isEqualTo(coachIdentifier)
        then(response.clientsStatus).isNull()

    }

    @Test
    fun `test get incomes statistics`() {

        val coachIdentifier = UUID.randomUUID()
        val from = "2021-12-10"
        val to = "2021-12-20"
        val filter = MetricsFilter(coachIdentifier).apply {
            dateInterval = DateIntervalDto(DateHelper.toDate(from), DateHelper.toDate(to))
        }

        every { incomesMetricsService.getMetrics(filter) } returns
                MetricsDto().apply {
                    incomes = Incomes(
                        pending = 20,
                        accepted = 9,
                        rejected = 11,
                        total = 40
                    )
                }

        val response = metricsImpl.getIncomesStatistics(coachIdentifier, from, to)

        then(response.identifier).isEqualTo(coachIdentifier)
        then(response.incomesStatus?.accepted).isEqualTo(9)
        then(response.incomesStatus?.rejected).isEqualTo(11)
        then(response.incomesStatus?.pending).isEqualTo(20)

    }

    @Test
    fun `test get empty incomes statistics`() {

        val coachIdentifier = UUID.randomUUID()
        val from = "2021-12-10"
        val to = "2021-12-20"
        val filter = MetricsFilter(coachIdentifier).apply {
            dateInterval = DateIntervalDto(DateHelper.toDate(from), DateHelper.toDate(to))
        }

        every { incomesMetricsService.getMetrics(filter) } returns MetricsDto()

        val response = metricsImpl.getIncomesStatistics(coachIdentifier, from, to)

        then(response.identifier).isEqualTo(coachIdentifier)
        then(response.incomesStatus).isNull()

    }

}
