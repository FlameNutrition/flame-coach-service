package com.coach.flame.jpa.entity.maker

import com.coach.flame.jpa.entity.Coach
import com.coach.flame.jpa.entity.RegistrationInvite
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import com.natpryce.makeiteasy.Property
import java.time.LocalDateTime

object RegistrationInviteBuilder {

    private val MAKER: Maker<RegistrationInvite> = an(RegistrationInviteMaker.RegistrationInvite)

    fun maker(): Maker<RegistrationInvite> {
        return MAKER
    }

    fun default(): RegistrationInvite {
        return maker().make()
    }

}

class RegistrationInviteMaker {

    companion object {

        private val fake = Faker()
        val coachProp: Property<RegistrationInvite, Coach> = Property.newProperty()
        val sendToProp: Property<RegistrationInvite, String> = Property.newProperty()
        val registrationKeyProp: Property<RegistrationInvite, String> = Property.newProperty()
        val sendDttmProp: Property<RegistrationInvite, LocalDateTime> = Property.newProperty()
        val acceptedDttmProp: Property<RegistrationInvite, LocalDateTime?> = Property.newProperty()

        val RegistrationInvite: Instantiator<RegistrationInvite> = Instantiator { instantiator ->
            RegistrationInvite().apply {
                coach = instantiator.valueOf(coachProp, CoachBuilder.default())
                sendTo = instantiator.valueOf(sendToProp, fake.internet().emailAddress())
                registrationKey = instantiator.valueOf(registrationKeyProp, "hhsdyeyyw222")
                sendDttm = instantiator.valueOf(sendDttmProp, LocalDateTime.now())
                acceptedDttm = instantiator.valueOf(acceptedDttmProp, null as LocalDateTime?)
            }
        }
    }

}
