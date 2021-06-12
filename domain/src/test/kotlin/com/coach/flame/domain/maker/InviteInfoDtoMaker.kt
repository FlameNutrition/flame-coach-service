package com.coach.flame.domain.maker

import com.coach.flame.domain.ClientStatusDto
import com.coach.flame.domain.InviteInfoDto
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.util.*

class InviteInfoDtoMaker {

    companion object {

        private val fake = Faker()
        val sender: Property<InviteInfoDto, UUID> = newProperty()
        val isRegistrationInvite: Property<InviteInfoDto, Boolean> = newProperty()
        val registrationKey: Property<InviteInfoDto, String?> = newProperty()
        val registrationLink: Property<InviteInfoDto, String?> = newProperty()
        val clientStatus: Property<InviteInfoDto, ClientStatusDto?> = newProperty()

        val InviteInfoDto: Instantiator<InviteInfoDto> = Instantiator {
            val inviteInfoDto = InviteInfoDto(
                sender = it.valueOf(sender, UUID.randomUUID()),
                isRegistrationInvite = it.valueOf(isRegistrationInvite, false),
            )
            inviteInfoDto.registrationLink = it.valueOf(registrationLink, null as String?)
            inviteInfoDto.registrationKey = it.valueOf(registrationKey, null as String?)
            inviteInfoDto.clientStatus = it.valueOf(clientStatus, null as ClientStatusDto?)
            inviteInfoDto
        }
    }

}
