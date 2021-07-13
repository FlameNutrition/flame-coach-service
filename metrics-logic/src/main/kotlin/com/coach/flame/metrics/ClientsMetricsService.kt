package com.coach.flame.metrics

import com.coach.flame.domain.MetricsDto
import java.util.*

interface ClientsMetricsService {

    fun getClientsMetrics(coachIdentifier: UUID): MetricsDto.Clients

}
