package com.coach.flame.testing.component.base.utils

import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.Coach
import com.coach.flame.jpa.entity.maker.CoachBuilder
import com.coach.flame.jpa.entity.maker.CoachMaker
import com.natpryce.makeiteasy.MakeItEasy.with
import java.util.*

object CoachHelper {

    val oneCoach: (UUID, MutableList<Client>) -> Coach = { uuid: UUID, listOfClients: MutableList<Client> ->
        val coach = CoachBuilder.maker()
            .but(with(CoachMaker.uuid, uuid))
            .make()

        listOfClients.forEach { it.coach = coach }

        coach.apply {
            clients = listOfClients
        }
    }

}
