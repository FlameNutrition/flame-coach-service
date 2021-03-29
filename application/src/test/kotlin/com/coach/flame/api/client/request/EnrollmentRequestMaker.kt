package com.coach.flame.api.client.request

import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.util.*

object EnrollmentRequestBuilder {

    private val MAKER: Maker<EnrollmentRequest> = an(EnrollmentRequestMaker.EnrollmentRequest)

    fun maker(): Maker<EnrollmentRequest> {
        return MAKER
    }

    fun default(): EnrollmentRequest {
        return maker().make()
    }

}

class EnrollmentRequestMaker {

    companion object {

        val client: Property<EnrollmentRequest, UUID?> = newProperty()
        val coach: Property<EnrollmentRequest, UUID?> = newProperty()
        val accept: Property<EnrollmentRequest, Boolean?> = newProperty()

        val EnrollmentRequest: Instantiator<EnrollmentRequest> = Instantiator {
            EnrollmentRequest(
                client = it.valueOf(client, null as UUID?),
                coach = it.valueOf(coach, null as UUID?),
                accept = it.valueOf(accept, null as Boolean?),
            )
        }
    }

}
