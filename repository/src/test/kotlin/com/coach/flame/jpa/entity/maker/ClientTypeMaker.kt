package com.coach.flame.jpa.entity.maker

import com.coach.flame.jpa.entity.ClientType
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty

class ClientTypeMaker {

    companion object {

        private val fake = Faker()
        val type: Property<ClientType, String> = newProperty()

        val ClientType: Instantiator<ClientType> = Instantiator {
            ClientType(
                type = it.valueOf(type, "CLIENT"),
            )
        }
    }

}
