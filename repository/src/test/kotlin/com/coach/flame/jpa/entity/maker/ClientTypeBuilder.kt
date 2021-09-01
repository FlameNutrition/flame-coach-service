package com.coach.flame.jpa.entity.maker

import com.coach.flame.jpa.entity.ClientType
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import com.natpryce.makeiteasy.Property

object ClientTypeBuilder {

    private val MAKER: Maker<ClientType> = an(ClientTypeMaker.ClientType)

    fun maker(): Maker<ClientType> {
        return MAKER
    }

    fun default(): ClientType {
        return maker().make()
    }

}

class ClientTypeMaker {

    companion object {

        private val fake = Faker()
        val type: Property<ClientType, String> = Property.newProperty()

        val ClientType: Instantiator<ClientType> = Instantiator {
            ClientType(
                type = it.valueOf(type, "CLIENT"),
            )
        }
    }

}
