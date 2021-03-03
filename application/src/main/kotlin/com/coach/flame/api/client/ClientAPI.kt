package com.coach.flame.api.client

import com.coach.flame.api.client.request.ClientRequest
import com.coach.flame.api.client.response.ClientResponse

interface ClientAPI {

    fun registerNewClient(client: ClientRequest): ClientResponse

}