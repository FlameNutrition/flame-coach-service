package com.coach.flame.customer.client

import com.coach.flame.domain.ClientDto
import java.util.*

interface ClientEnrollmentProcess {

    fun init(client: UUID, coach: UUID): ClientDto

    fun finish(client: UUID, accept: Boolean): ClientDto

    fun `break`(client: UUID): ClientDto

    fun status(client: UUID): ClientDto
}
