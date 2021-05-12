package com.coach.flame.api.coach.request

import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty

object ContactInfoRequestBuilder {

    private val MAKER: Maker<ContactInfoRequest> = an(ContactInfoRequestMaker.ContactInfoRequest)

    fun maker(): Maker<ContactInfoRequest> {
        return MAKER
    }

    fun default(): ContactInfoRequest {
        return maker().make()
    }

}

class ContactInfoRequestMaker {

    companion object {

        private val fake = Faker()
        val firstName: Property<ContactInfoRequest, String> = newProperty()
        val lastName: Property<ContactInfoRequest, String> = newProperty()
        val phoneCode: Property<ContactInfoRequest, String> = newProperty()
        val phoneNumber: Property<ContactInfoRequest, String> = newProperty()
        val countryCode: Property<ContactInfoRequest, String?> = newProperty()

        val ContactInfoRequest: Instantiator<ContactInfoRequest> = Instantiator {
            ContactInfoRequest(
                firstName = it.valueOf(firstName, fake.superhero().name()),
                lastName = it.valueOf(lastName, fake.superhero().name()),
                phoneCode = it.valueOf(phoneCode, fake.phoneNumber().extension()),
                phoneNumber = it.valueOf(phoneNumber, fake.phoneNumber().phoneNumber()),
                countryCode = it.valueOf(countryCode, null as String?),
            )
        }
    }

}
