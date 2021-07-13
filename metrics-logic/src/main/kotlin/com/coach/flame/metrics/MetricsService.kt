package com.coach.flame.metrics

import com.coach.flame.domain.MetricsDto
import org.springframework.stereotype.Service
import java.util.*

@Service
class MetricsService(
    private val clientsMetricsService: ClientsMetricsService,
) {

    fun getMetrics(coachIdentifier: UUID): MetricsDto {
        val clientsMetrics = clientsMetricsService.getClientsMetrics(coachIdentifier)
        return MetricsDto(clientsMetrics)
    }

}
