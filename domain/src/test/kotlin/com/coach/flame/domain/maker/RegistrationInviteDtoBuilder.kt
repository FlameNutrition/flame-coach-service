package com.coach.flame.domain.maker

import com.coach.flame.domain.CoachDto
import com.coach.flame.domain.RegistrationInviteDto
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import com.natpryce.makeiteasy.Property
import java.time.LocalDateTime

object RegistrationInviteDtoBuilder {

    private val MAKER: Maker<RegistrationInviteDto> = an(RegistrationInviteDtoMaker.RegistrationInviteDto)

    fun maker(): Maker<RegistrationInviteDto> {
        return MAKER
    }

    fun default(): RegistrationInviteDto {
        return maker().make()
    }

}

class RegistrationInviteDtoMaker {

    companion object {

        private val fake = Faker()
        val id: Property<RegistrationInviteDto, Long?> = Property.newProperty()
        val sender: Property<RegistrationInviteDto, CoachDto> = Property.newProperty()
        val sendTo: Property<RegistrationInviteDto, String> = Property.newProperty()
        val registrationLink: Property<RegistrationInviteDto, String> = Property.newProperty()
        val registrationKey: Property<RegistrationInviteDto, String> = Property.newProperty()
        val sendDttm: Property<RegistrationInviteDto, LocalDateTime> = Property.newProperty()
        val acceptedDttm: Property<RegistrationInviteDto, LocalDateTime?> = Property.newProperty()

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
