package com.coach.flame.jpa.entity

import com.coach.flame.date.DateHelper.toAnotherZone
import com.coach.flame.domain.AppointmentDto
import com.coach.flame.jpa.entity.Client.Companion.toClient
import com.coach.flame.jpa.entity.Coach.Companion.toCoach
import com.coach.flame.jpa.entity.Income.Companion.toIncome
import org.hibernate.annotations.Type
import org.springframework.data.jpa.domain.AbstractPersistable
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.persistence.*

@Table(name = "Appointment")
@Entity
class Appointment(

    @Type(type = "uuid-char")
    @Column(name = "uuid", nullable = false, unique = true)
    val uuid: UUID,

    @Column(name = "dttmStarts", nullable = false)
    var dttmStarts: LocalDateTime,

    @Column(name = "dttmEnds", nullable = false)
    var dttmEnds: LocalDateTime,

    @Column(name = "\"delete\"", nullable = false, columnDefinition = "tinyint(1) default 0")
    var delete: Boolean,

    @ManyToOne(optional = false)
    @JoinColumn(name = "clientFk", nullable = false)
    var client: Client,

    @ManyToOne(optional = false)
    @JoinColumn(name = "coachFk", nullable = false)
    var coach: Coach,

    @Column(name = "currency", nullable = false, length = 3)
    var currency: String,

    @OneToOne(orphanRemoval = true, cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH])
    @JoinColumn(name = "incomeFk", referencedColumnName = "id")
    var income: Income,

    @Column(name = "notes")
    var notes: String? = null,
) : AbstractPersistable<Long>() {

    fun toDto(zoneId: ZoneId): AppointmentDto {
        return AppointmentDto(
            id = this.id,
            identifier = this.uuid,
            coach = this.coach.toDto(),
            client = this.client.toDto(),
            delete = this.delete,
            dttmStarts = toAnotherZone(this.dttmStarts, zoneId),
            dttmEnds = toAnotherZone(this.dttmEnds, zoneId),
            currency = Currency.getInstance(this.currency),
            income = this.income.toDto(),
            notes = this.notes
        )
    }

    fun toDto(): AppointmentDto {
        return toDto(ZoneId.systemDefault())
    }

    companion object {
        fun AppointmentDto.toAppointment(): Appointment {

            checkNotNull(coach) { "coach can not be null" }
            checkNotNull(client) { "client can not be null" }

            val appointment = Appointment(
                uuid = identifier,
                coach = coach!!.toCoach(),
                client = client!!.toClient(),
                dttmStarts = toAnotherZone(dttmStarts, ZoneId.systemDefault()).toLocalDateTime(),
                dttmEnds = toAnotherZone(dttmEnds, ZoneId.systemDefault()).toLocalDateTime(),
                delete = delete,
                currency = currency.currencyCode,
                income = income.toIncome(),
                notes = notes
            )

            appointment.id = id

            return appointment
        }
    }
}


