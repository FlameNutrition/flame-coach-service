package com.coach.flame.jpa.entity.maker

import com.coach.flame.jpa.entity.ClientMeasureWeight
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.time.LocalDate

class ClientMeasureWeightMaker {

    companion object {

        private val fake = Faker()
        val weight: Property<ClientMeasureWeight, Float> = newProperty()
        val measureDate: Property<ClientMeasureWeight, LocalDate> = newProperty()

        val ClientMeasureWeight: Instantiator<ClientMeasureWeight> = Instantiator {
            ClientMeasureWeight(
                weight = it.valueOf(weight, fake.number().randomNumber().toFloat()),
                measureDate = it.valueOf(measureDate, LocalDate.now())
            )
        }
    }

}
