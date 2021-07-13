package com.coach.flame.jpa.repository.operations

import com.coach.flame.domain.CoachDto
import java.util.*

interface CoachRepositoryOperation {

    fun getCoach(identifier: UUID): CoachDto

}
