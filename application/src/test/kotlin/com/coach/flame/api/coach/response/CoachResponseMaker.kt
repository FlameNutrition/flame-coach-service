package com.coach.flame.api.coach.response

import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.util.*

class CoachResponseMaker {

    companion object {

        val identifier: Property<CoachResponse, UUID> = newProperty()
        val listOfClients: Property<CoachResponse, Set<ClientCoach>> = newProperty()

        val CoachResponse: Instantiator<CoachResponse> = Instantiator {
            CoachResponse(
                identifier = it.valueOf(identifier, UUID.randomUUID()),
                clientsCoach = it.valueOf(listOfClients, setOf())
            )
        }
    }

}