package com.coach.flame.domain.maker

import com.coach.flame.domain.GenderDto
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import com.natpryce.makeiteasy.Property

object GenderDtoBuilder {

    private val MAKER: Maker<GenderDto> = an(GenderDtoMaker.GenderDto)

    fun maker(): Maker<GenderDto> {
        return MAKER
    }

    fun default(): GenderDto {
        return maker().make()
    }

}

class GenderDtoMaker {

    companion object {

        private val fake = Faker()
        val id: Property<GenderDto, Long?> = Property.newProperty()
        val genderCode: Property<GenderDto, String> = Property.newProperty()
        val externalValue: Property<GenderDto, String> = Property.newProperty()

        val GenderDto: Instantiator<GenderDto> = Instantiator {

            val gender = fake.demographic().sex()

            GenderDto(
                id = it.valueOf(id, null as Long?),
                genderCode = it.valueOf(genderCode, gender),
                externalValue = it.valueOf(externalValue, gender.first().toString())
            )
        }
    }

}
