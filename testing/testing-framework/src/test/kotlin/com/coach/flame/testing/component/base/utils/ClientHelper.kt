package com.coach.flame.testing.component.base.utils

import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.ClientStatus
import com.coach.flame.jpa.entity.Coach
import com.coach.flame.jpa.entity.maker.ClientBuilder
import com.coach.flame.jpa.entity.maker.ClientMaker
import com.coach.flame.jpa.entity.maker.CoachBuilder
import com.coach.flame.jpa.entity.maker.CoachMaker
import com.natpryce.makeiteasy.MakeItEasy.with
import java.util.*

object ClientHelper {

    val oneClientPending: (UUID) -> Client = { uuid: UUID ->
        ClientBuilder.maker()
            .but(with(ClientMaker.uuid, uuid),
                with(ClientMaker.clientStatus, ClientStatus.PENDING))
            .make()
    }

    val oneClientAvailable: (UUID) -> Client = { uuid: UUID ->
        ClientBuilder.maker()
            .but(with(ClientMaker.uuid, uuid),
                with(ClientMaker.clientStatus, ClientStatus.AVAILABLE))
            .make()
    }

}
