package com.coach.flame.domain

import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker

object ClientDtoBuilder {

    private val MAKER: Maker<ClientDto> = an(ClientDtoMaker.ClientDto)

    fun maker(): Maker<ClientDto> {
        return MAKER
    }

    fun default(): ClientDto {
        return maker().make()
    }

}
