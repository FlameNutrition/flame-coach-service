package com.coach.flame.api.appointment

import com.coach.flame.api.appointment.response.IncomeResponse
import java.util.*

interface IncomeAPI {

    fun getAcceptedIncomes(coachIdentifier: UUID, from: String, to: String, aggregateType: String): IncomeResponse
}
