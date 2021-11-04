package com.coach.flame.testing.component.base.utils

import com.coach.flame.domain.ClientStatusDto
import com.coach.flame.domain.maker.ClientDtoBuilder
import com.coach.flame.domain.maker.ClientDtoMaker
import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.Client.Companion.toClient
import com.natpryce.makeiteasy.MakeItEasy.with
import java.util.*

object ClientHelper {

    val oneClientPending: (UUID) -> Client = { uuid: UUID ->
        ClientDtoBuilder.makerWithLoginInfo()
            .but(
                with(ClientDtoMaker.identifier, uuid),
                with(ClientDtoMaker.clientStatus, ClientStatusDto.PENDING)
            )
            .make()
            .toClient()
    }

    val oneClientAvailable: (UUID) -> Client = { uuid: UUID ->
        ClientDtoBuilder.makerWithLoginInfo()
            .but(
                with(ClientDtoMaker.identifier, uuid),
                with(ClientDtoMaker.clientStatus, ClientStatusDto.AVAILABLE)
            )
            .make()
            .toClient()
    }

}
