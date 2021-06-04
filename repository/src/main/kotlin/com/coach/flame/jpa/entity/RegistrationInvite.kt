package com.coach.flame.jpa.entity

import com.coach.flame.domain.RegistrationInviteDto
import com.coach.flame.jpa.entity.Coach.Companion.toCoach
import org.springframework.data.jpa.domain.AbstractPersistable
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "Registration_Invite")
class RegistrationInvite : AbstractPersistable<Long>() {

    @JoinColumn(name = "coachFk", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    var coach: Coach? = null

    @Column(name = "sendTo", nullable = false)
    var sendTo: String? = null

    @Column(name = "registrationKey", nullable = false, unique = true)
    var registrationKey: String? = null

    @Column(name = "sendDttm", nullable = false)
    var sendDttm: LocalDateTime? = null

    @Column(name = "acceptedDttm")
    var acceptedDttm: LocalDateTime? = null

    fun toDto(registrationLink: String): RegistrationInviteDto {
        return RegistrationInviteDto(
            sender = coach!!.toDto(),
            sendTo = sendTo!!,
            registrationKey = registrationKey!!,
            registrationLink = registrationLink,
            sendDttm = sendDttm!!,
            acceptedDttm = acceptedDttm)
    }

    companion object {
        fun RegistrationInviteDto.toRegistrationInvite(): RegistrationInvite {
            val registrationInvite = RegistrationInvite()
            registrationInvite.id = id
            registrationInvite.coach = sender.toCoach()
            registrationInvite.sendTo = sendTo
            registrationInvite.registrationKey = registrationKey
            registrationInvite.sendDttm = sendDttm
            registrationInvite.acceptedDttm = acceptedDttm

            return registrationInvite
        }
    }

}
