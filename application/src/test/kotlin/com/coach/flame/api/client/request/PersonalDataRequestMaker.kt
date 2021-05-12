package com.coach.flame.api.client.request

import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty

object PersonalDataRequestBuilder {

    private val MAKER: Maker<PersonalDataRequest> = an(PersonalDataRequestMaker.PersonalDataRequest)

    fun maker(): Maker<PersonalDataRequest> {
        return MAKER
    }

    fun default(): PersonalDataRequest {
        return maker().make()
    }

}

class PersonalDataRequestMaker {

    companion object {

        private val fake = Faker()
        val measureTypeCode: Property<PersonalDataRequest, String> = newProperty()
        val weight: Property<PersonalDataRequest, Float> = newProperty()
        val height: Property<PersonalDataRequest, Float> = newProperty()
        val genderCode: Property<PersonalDataRequest, String?> = newProperty()

        val PersonalDataRequest: Instantiator<PersonalDataRequest> = Instantiator {
            PersonalDataRequest(
                measureTypeCode = it.valueOf(measureTypeCode, "KG_CM"),
                weight = it.valueOf(weight, fake.number().randomNumber().toFloat()),
                height = it.valueOf(height, fake.number().randomNumber().toFloat()),
                genderCode = it.valueOf(genderCode, null as String?)
            )
        }
    }

}
