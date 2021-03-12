package com.coach.flame.jpa.entity

import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker

object ClientBuilder {

    fun maker(): Maker<Client> {
        return an(ClientMaker.Client)
    }

    fun default(): Client {
        return maker().make()
    }

}
