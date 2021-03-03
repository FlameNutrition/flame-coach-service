package com.coach.flame.client

import com.coach.flame.domain.ClientDto
import java.util.*

//TODO: Write documentation
interface ClientService {

    fun getClient(uuid: UUID) : ClientDto

    fun registerClient(clientDto: ClientDto): ClientDto

}