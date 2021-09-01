package com.coach.flame.jpa.entity.maker

import com.coach.flame.jpa.entity.ClientMeasureWeight
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import com.natpryce.makeiteasy.Property
import java.time.LocalDate

object ClientMeasureWeightBuilder {

    private val MAKER: Maker<ClientMeasureWeight> = an(ClientMeasureWeightMaker.ClientMeasureWeight)

    fun maker(): Maker<ClientMeasureWeight> {
        return MAKER
    }

    fun default(): ClientMeasureWeight {
        return maker().make()
    }

}

class ClientMeasureWeightMaker {

    companion object {

        private val fake = Faker()
        val weight: Property<ClientMeasureWeight, Float> = Property.newProperty()
        val measureDate: Property<ClientMeasureWeight, LocalDate> = Property.newProperty()

        val ClientMeasureWeight: Instantiator<ClientMeasureWeight> = Instantiator {
            ClientMeasureWeight(
                weight = it.valueOf(weight, fake.number().randomNumber().toFloat()),
                measureDate = it.valueOf(measureDate, LocalDate.now())
            )
        }
    }

}
