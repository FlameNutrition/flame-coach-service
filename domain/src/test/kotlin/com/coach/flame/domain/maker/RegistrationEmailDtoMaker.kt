package com.coach.flame.domain.maker

import com.coach.flame.domain.CoachDto
import com.coach.flame.domain.RegistrationInviteDto
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.time.LocalDateTime

class RegistrationEmailDtoMaker {

    companion object {

        private val fake = Faker()
        val id: Property<RegistrationInviteDto, Long?> = newProperty()
        val sender: Property<RegistrationInviteDto, CoachDto> = newProperty()
        val sendTo: Property<RegistrationInviteDto, String> = newProperty()
        val registrationLink: Property<RegistrationInviteDto, String> = newProperty()
        val registrationKey: Property<RegistrationInviteDto, String> = newProperty()
        val sendDttm: Property<RegistrationInviteDto, LocalDateTime> = newProperty()
        val acceptedDttm: Property<RegistrationInviteDto, LocalDateTime?> = newProperty()

        val RegistrationInviteDto: Instantiator<RegistrationInviteDto> = Instantiator {
            RegistrationInviteDto(
                id = it.valueOf(id, null as Long?),
                sender = it.valueOf(sender, CoachDtoBuilder.default()),
                sendTo = it.valueOf(sendTo, fake.internet().emailAddress()),
                registrationLink = it.valueOf(registrationLink, "http://localhost:8080/register=435sdfaeqwr2!"),
                registrationKey = it.valueOf(registrationKey, "435sdfaeqwr2!"),
                sendDttm = it.valueOf(sendDttm, LocalDateTime.now()),
                acceptedDttm = it.valueOf(acceptedDttm, null as LocalDateTime?),
            )
        }
    }

}
