package com.coach.flame.jpa.entity.maker

import com.coach.flame.jpa.entity.Coach
import com.coach.flame.jpa.entity.RegistrationInvite
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.time.LocalDateTime

class RegistrationInviteMaker {

    companion object {

        private val fake = Faker()
        val coachProp: Property<RegistrationInvite, Coach> = newProperty()
        val sendToProp: Property<RegistrationInvite, String> = newProperty()
        val registrationKeyProp: Property<RegistrationInvite, String> = newProperty()
        val sendDttmProp: Property<RegistrationInvite, LocalDateTime> = newProperty()
        val acceptedDttmProp: Property<RegistrationInvite, LocalDateTime?> = newProperty()

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
