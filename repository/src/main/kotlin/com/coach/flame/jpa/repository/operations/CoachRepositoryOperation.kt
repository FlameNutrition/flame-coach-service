package com.coach.flame.jpa.repository.operations

import com.coach.flame.jpa.entity.Coach
import java.util.*

interface CoachRepositoryOperation {

    fun getCoach(identifier: UUID): Coach

}
