package com.coach.flame.metrics

import com.coach.flame.domain.MetricsDto
import java.util.*

interface MetricsService {

    fun getMetrics(metricsFilter: MetricsFilter): MetricsDto

}
