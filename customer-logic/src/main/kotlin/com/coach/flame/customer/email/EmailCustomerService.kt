package com.coach.flame.customer.email

import com.coach.flame.domain.CoachDto
import com.coach.flame.domain.RegistrationInviteDto

interface EmailCustomerService {

    fun sendRegistrationLink(coachDto: CoachDto, clientEmail: String): RegistrationInviteDto

}
