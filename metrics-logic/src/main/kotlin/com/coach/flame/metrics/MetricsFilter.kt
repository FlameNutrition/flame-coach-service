package com.coach.flame.metrics

import com.coach.flame.domain.DateIntervalDto
import java.util.*

data class MetricsFilter(val identifier: UUID) {

    var dateInterval: DateIntervalDto? = null

}
