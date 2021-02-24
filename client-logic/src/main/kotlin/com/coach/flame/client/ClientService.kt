package com.coach.flame.client

import java.util.*

//TODO: Write documentation
interface ClientService {

    fun getClient(uuid: UUID)

    fun registerClient(taskId: Long)

}