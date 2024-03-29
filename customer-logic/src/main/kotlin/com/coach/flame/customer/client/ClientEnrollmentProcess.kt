package com.coach.flame.customer.client

import com.coach.flame.domain.ClientDto
import java.util.*

interface ClientEnrollmentProcess {

    fun init(client: ClientDto, coach: UUID): ClientDto

    fun finish(client: ClientDto, accept: Boolean): ClientDto

    fun `break`(client: ClientDto): ClientDto

}
