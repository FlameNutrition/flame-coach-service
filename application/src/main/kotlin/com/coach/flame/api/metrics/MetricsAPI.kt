package com.coach.flame.api.metrics

import com.coach.flame.api.metrics.response.StatisticsResponse
import java.util.*

//TODO: Write documentation
interface MetricsAPI {

    fun getClientsStatistics(coachIdentifier: UUID): StatisticsResponse

    fun getIncomesStatistics(coachIdentifier: UUID, from: String, to: String): StatisticsResponse

}
