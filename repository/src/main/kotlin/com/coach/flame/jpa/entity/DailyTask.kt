package com.coach.flame.jpa.entity

import com.coach.flame.domain.DailyTaskDto
import com.coach.flame.jpa.entity.Client.Companion.toClient
import com.coach.flame.jpa.entity.Coach.Companion.toCoach
import org.hibernate.annotations.Type
import org.springframework.data.jpa.domain.AbstractPersistable
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "Daily_Task")
class DailyTask(

    @Type(type = "uuid-char")
    @Column(nullable = false, unique = true)
    val uuid: UUID,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var description: String,

    @Column(nullable = false)
    var date: LocalDate,

    @Column(nullable = false)
    var ticked: Boolean,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "createdByFk", referencedColumnName = "id")
    val createdBy: Coach,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "clientFk", referencedColumnName = "id")
    val client: Client,
) : AbstractPersistable<Long>() {

    fun toDto(): DailyTaskDto {

        val coach = this.createdBy.toDto()

        return DailyTaskDto(
            id = this.id,
            identifier = this.uuid,
            name = this.name,
            description = this.description,
            date = this.date,
            ticked = this.ticked,
            coachIdentifier = this.createdBy.uuid,
            clientIdentifier = this.client.uuid,
            coach = coach,
            client = this.client.toDto(coach)
        )
    }

    companion object {
        fun DailyTaskDto.toDailyTask(): DailyTask {

            requireNotNull(coach) { "coach can not be null" }
            requireNotNull(client) { "client can not be null" }

            val dailyTask = DailyTask(
                uuid = identifier,
                name = name,
                description = description,
                date = date,
                ticked = ticked,
                createdBy = coach!!.toCoach(),
                client = client!!.toClient())

            dailyTask.id = id
            return dailyTask
        }
    }
}
