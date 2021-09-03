package com.coach.flame.jpa.repository.operations

import com.coach.flame.jpa.entity.Coach
import com.coach.flame.jpa.entity.Income
import java.time.LocalDate
import java.util.*

interface CoachRepositoryOperation {

    fun getCoach(identifier: UUID): Coach

    fun getIncome(identifier: UUID, from: LocalDate, to: LocalDate): List<Income>

}
