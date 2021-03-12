package com.coach.flame.domain

import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker

object ClientDtoBuilder {

    fun maker(): Maker<ClientDto> {
        return an(ClientDtoMaker.ClientDto)
    }

    fun default(): ClientDto {
        return maker().make()
    }

}
