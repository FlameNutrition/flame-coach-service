package com.coach.flame.jpa.entity

import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty

class GenderMaker {

    companion object {

        private val fake = Faker()
        val genderCode: Property<GenderConfig, String> = newProperty()
        val externalValue: Property<GenderConfig, String> = newProperty()

        val GenderConfig: Instantiator<GenderConfig> = Instantiator {

            val gender = fake.demographic().sex()

            GenderConfig(
                genderCode = it.valueOf(genderCode, gender),
                externalValue = it.valueOf(externalValue, gender.first().toString())
            )
        }
    }

}