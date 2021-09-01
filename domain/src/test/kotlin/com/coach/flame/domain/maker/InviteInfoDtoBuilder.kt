package com.coach.flame.domain.maker

import com.coach.flame.domain.ClientStatusDto
import com.coach.flame.domain.InviteInfoDto
import com.coach.flame.domain.LoginInfoDto
import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.Maker
import com.natpryce.makeiteasy.Property
import java.util.*

object InviteInfoDtoBuilder {

    private val MAKER: Maker<InviteInfoDto> = an(InviteInfoDtoMaker.InviteInfoDto)

    fun maker(): Maker<InviteInfoDto> {
        return MAKER
    }

    fun default(): InviteInfoDto {
        return maker().make()
    }

}

class InviteInfoDtoMaker {

    companion object {

        private val fake = Faker()
        val sender: Property<InviteInfoDto, UUID> = Property.newProperty()
        val isRegistrationInvite: Property<InviteInfoDto, Boolean> = Property.newProperty()
        val registrationKey: Property<InviteInfoDto, String?> = Property.newProperty()
        val registrationLink: Property<InviteInfoDto, String?> = Property.newProperty()
        val clientStatus: Property<InviteInfoDto, ClientStatusDto?> = Property.newProperty()

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
