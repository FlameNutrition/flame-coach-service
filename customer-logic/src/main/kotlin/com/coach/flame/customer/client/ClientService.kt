package com.coach.flame.customer.client

import com.coach.flame.domain.ClientDto
import java.util.*

interface ClientService {

    fun getAllClients(): Set<ClientDto>

    fun getAllClientsFromCoach(uuid: UUID): Set<ClientDto>

    fun getAllClientsForCoach(uuid: UUID): Set<ClientDto>

}