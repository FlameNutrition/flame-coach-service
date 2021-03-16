package com.coach.flame.jpa.entity

import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker

object ClientTypeBuilder {

    private val MAKER: Maker<ClientType> = an(ClientTypeMaker.ClientType)

    fun maker(): Maker<ClientType> {
        return MAKER
    }

    fun default(): ClientType {
        return maker().make()
    }

}
