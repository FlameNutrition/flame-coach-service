package com.coach.flame.metrics

import com.coach.flame.domain.ClientStatusDto
import com.coach.flame.domain.MetricsDto
import com.coach.flame.jpa.repository.operations.CoachRepositoryOperation
import org.springframework.stereotype.Service
import java.util.*

@Service
class ClientsMetricsServiceImpl(
    private val coachOperations: CoachRepositoryOperation,
) : ClientsMetricsService {

    override fun getClientsMetrics(coachIdentifier: UUID): MetricsDto.Clients {

        val coach = coachOperations.getCoach(coachIdentifier).toDto()

        val numberOfPending = coach.listOfClients.count { ClientStatusDto.PENDING === it.clientStatus }
        val numberOfAccepted = coach.listOfClients.count { ClientStatusDto.ACCEPTED === it.clientStatus }

        return MetricsDto.Clients(
            numberOfTotalClients = coach.listOfClients.size,
            numberOfClientsPending = numberOfPending,
            numberOfClientsAccepted = numberOfAccepted
        )
    }

}
