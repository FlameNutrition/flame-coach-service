package com.coach.flame.metrics.services

import com.coach.flame.domain.ClientStatusDto
import com.coach.flame.domain.MetricsDto
import com.coach.flame.domain.metrics.Clients
import com.coach.flame.jpa.repository.operations.CoachRepositoryOperation
import com.coach.flame.metrics.MetricsFilter
import com.coach.flame.metrics.MetricsService
import org.springframework.stereotype.Service

@Service("clientsMetricsService")
class ClientsMetricsServiceImpl(
    private val coachOperations: CoachRepositoryOperation,
) : MetricsService {

    override fun getMetrics(metricsFilter: MetricsFilter): MetricsDto {

        val coach = coachOperations.getCoach(metricsFilter.identifier).toDto()

        val numberOfPending = coach.listOfClients.count { ClientStatusDto.PENDING === it.clientStatus }
        val numberOfAccepted = coach.listOfClients.count { ClientStatusDto.ACCEPTED === it.clientStatus }

        return MetricsDto().apply {
            clients = Clients(
                numberOfTotalClients = coach.listOfClients.size,
                numberOfClientsPending = numberOfPending,
                numberOfClientsAccepted = numberOfAccepted
            )
        }
    }

}
