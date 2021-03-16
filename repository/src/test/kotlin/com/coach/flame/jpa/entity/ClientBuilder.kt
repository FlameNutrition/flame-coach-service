package com.coach.flame.jpa.entity

import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker

object ClientBuilder {

    private val MAKER: Maker<Client> = an(ClientMaker.Client)

    fun maker(): Maker<Client> {
        return MAKER
    }

    fun default(): Client {
        return maker().make()
    }

}
