package com.coach.flame.jpa.entity

import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker

object ClientTypeBuilder {

    fun maker(): Maker<ClientType> {
        return an(ClientTypeMaker.ClientType)
    }

    fun default(): ClientType {
        return maker().make()
    }

}
