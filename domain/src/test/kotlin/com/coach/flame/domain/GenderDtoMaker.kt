package com.coach.flame.domain

import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import com.natpryce.makeiteasy.PropertyLookup
import java.util.*

class GenderDtoMaker {

    companion object {

        private val fake = Faker()
        val genderCode: Property<GenderDto, String> = newProperty()
        val externalValue: Property<GenderDto, String> = newProperty()

        val GenderDto: Instantiator<GenderDto> = Instantiator {

            val gender = fake.demographic().sex()

            GenderDto(
                genderCode = it.valueOf(genderCode, gender),
                externalValue = it.valueOf(externalValue, gender.first().toString())
            )
        }
    }

}