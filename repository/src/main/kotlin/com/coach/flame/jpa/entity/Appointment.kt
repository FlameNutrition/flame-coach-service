package com.coach.flame.jpa.entity

import com.coach.flame.domain.AppointmentDto
import com.coach.flame.jpa.entity.Client.Companion.toClient
import com.coach.flame.jpa.entity.Coach.Companion.toCoach
import org.hibernate.annotations.Type
import org.springframework.data.jpa.domain.AbstractPersistable
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Table(name = "Appointment")
@Entity
class Appointment(

    @Type(type = "uuid-char")
    @Column(name = "uuid", nullable = false, unique = true)
    val uuid: UUID,

    @Column(name = "dttm", nullable = false)
    var dttm: LocalDateTime,

    @Column(name = "\"delete\"", nullable = false, columnDefinition = "tinyint(1) default 0")
    var delete: Boolean,

    @Column(name = "price", nullable = false, columnDefinition = "float default '0.0'")
    var price: Float = 0.0f,

    @ManyToOne(optional = false)
    @JoinColumn(name = "clientFk", nullable = false)
    var client: Client,

    @ManyToOne(optional = false)
    @JoinColumn(name = "coachFk", nullable = false)
    var coach: Coach,

    @Column(name = "currency", nullable = false, length = 3)
    var currency: String,

    @Column(name = "notes")
    var notes: String? = null,
) : AbstractPersistable<Long>() {

    fun toDto(): AppointmentDto {

        return AppointmentDto(
            id = this.id,
            identifier = this.uuid,
            coach = this.coach.toDto(),
            client = this.client.toDto(),
            price = this.price,
            delete = this.delete,
            dttm = this.dttm,
            currency = Currency.getInstance(this.currency),
            notes = this.notes
        )
    }

    companion object {
        fun AppointmentDto.toAppointment(): Appointment {

            checkNotNull(coach) { "coach can not be null" }
            checkNotNull(client) { "client can not be null" }
            checkNotNull(dttm) { "dttm can not be null" }

            val appointment = Appointment(
                uuid = identifier,
                coach = coach!!.toCoach(),
                client = client!!.toClient(),
                dttm = dttm!!,
                delete = delete,
                price = price,
                currency = currency.currencyCode,
                notes = notes
            )

            appointment.id = id

            return appointment
        }
    }

}


