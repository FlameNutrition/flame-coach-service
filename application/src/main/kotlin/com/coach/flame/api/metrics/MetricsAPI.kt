package com.coach.flame.api.metrics

import com.coach.flame.api.metrics.response.ClientsStatisticsResponse
import java.util.*

//TODO: Write documentation
interface MetricsAPI {

    fun getClientsStatistics(coachIdentifier: UUID): ClientsStatisticsResponse

}
