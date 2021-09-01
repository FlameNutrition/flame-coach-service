package com.coach.flame.jpa.entity.maker

import com.coach.flame.jpa.entity.GenderConfig
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import com.natpryce.makeiteasy.Property

object GenderBuilder {

    private val MAKER: Maker<GenderConfig> = an(GenderMaker.GenderConfig)

    fun maker(): Maker<GenderConfig> {
        return MAKER
    }

    fun default(): GenderConfig {
        return maker().make()
    }

}

class GenderMaker {

    companion object {

        private val fake = Faker()
        val genderCode: Property<GenderConfig, String> = Property.newProperty()
        val externalValue: Property<GenderConfig, String> = Property.newProperty()

        val GenderConfig: Instantiator<GenderConfig> = Instantiator {

            val gender = fake.demographic().sex()

            GenderConfig(
                genderCode = it.valueOf(genderCode, gender),
                externalValue = it.valueOf(externalValue, gender.first().toString())
            )
        }
    }

}
