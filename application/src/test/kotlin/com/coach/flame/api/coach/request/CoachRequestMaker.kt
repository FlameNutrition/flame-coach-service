package com.coach.flame.api.coach.request

import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.util.*

class CoachRequestMaker {

    companion object {

        val identifier: Property<CoachRequest, UUID> = newProperty()

        val CoachRequest: Instantiator<CoachRequest> = Instantiator {
            CoachRequest(
                identifier = it.valueOf(identifier, UUID.randomUUID()),
            )
        }
    }

}