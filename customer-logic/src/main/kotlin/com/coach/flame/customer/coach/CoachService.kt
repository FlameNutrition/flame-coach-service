package com.coach.flame.customer.coach

import com.coach.flame.domain.CoachDto
import java.util.*

interface CoachService {

    fun getCoachWithClientsAccepted(uuid: UUID): CoachDto

    fun getCoachWithClientsAvailable(uuid:UUID) : CoachDto

}