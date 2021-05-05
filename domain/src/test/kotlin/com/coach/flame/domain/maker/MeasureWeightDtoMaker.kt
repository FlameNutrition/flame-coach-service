package com.coach.flame.domain.maker

import com.coach.flame.domain.MeasureWeightDto
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.time.LocalDate

class MeasureWeightDtoMaker {

    companion object {

        private val fake = Faker()
        val id: Property<MeasureWeightDto, Long?> = newProperty()
        val date: Property<MeasureWeightDto, LocalDate> = newProperty()
        val value: Property<MeasureWeightDto, Float> = newProperty()

        val MeasureWeightDto: Instantiator<MeasureWeightDto> = Instantiator {
            MeasureWeightDto(
                id = it.valueOf(id, null as Long?),
                date = it.valueOf(date, LocalDate.now()),
                value = it.valueOf(value, fake.number().randomNumber().toFloat())
            )
        }
    }

}
