package com.coach.flame.domain

import com.coach.flame.domain.metrics.Clients
import com.coach.flame.domain.metrics.Incomes

class MetricsDto {

    var clients: Clients? = null

    var incomes: Incomes? = null

    override fun toString(): String {
        return "MetricsDto(" +
                "clients=$clients, " +
                "incomes=$incomes" +
                ")"
    }

}
