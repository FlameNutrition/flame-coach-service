package com.coach.flame.customer.client

import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.ClientStatusDto
import java.util.*

interface ClientService {

    fun getAllClients(): Set<ClientDto>

    fun getAllClientsFromCoach(uuid: UUID): Set<ClientDto>

    fun getAllClientsForCoach(uuid: UUID): Set<ClientDto>

    fun updateClientStatus(uuid: UUID, status: ClientStatusDto): ClientDto

    fun linkCoach(uuidClient: UUID, uuidCoach: UUID): ClientDto

    fun unlinkCoach(uuidClient: UUID): ClientDto
}
