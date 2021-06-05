package com.coach.flame.customer.register

import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.CoachDto
import com.coach.flame.domain.RegistrationInviteDto

interface RegistrationCustomerService {

    fun sendRegistrationLink(coachDto: CoachDto, clientEmail: String): RegistrationInviteDto

    fun checkRegistrationLink(clientDto: ClientDto): Boolean

    fun updateRegistration(clientDto: ClientDto): RegistrationInviteDto
}
