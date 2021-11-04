package com.coach.flame.jpa.entity

import com.coach.flame.domain.MessageDto
import com.coach.flame.domain.RegistrationInviteDto
import com.coach.flame.jpa.entity.Coach.Companion.toCoach
import org.hibernate.annotations.Type
import org.springframework.lang.Nullable
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Table(name = "Message")
@Entity
class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Message_Generator")
    @SequenceGenerator(name = "Message_Generator", sequenceName = "Message_Seq", allocationSize = 1)
    @Nullable
    @Column(name = "id", nullable = false)
    internal var id: Long? = null

    @ManyToOne(optional = false)
    @JoinColumn(name = "clientFk", nullable = false)
    var client: Client? = null

    @ManyToOne(optional = false)
    @JoinColumn(name = "coachFk", nullable = false)
    var coach: Coach? = null

    @Column(name = "owner", nullable = false, length = 100)
    var owner: String? = null

    @Column(name = "time", nullable = false)
    var time: LocalDateTime? = null

    @Type(type = "uuid-char")
    @Column(name = "uuid", nullable = false, unique = true)
    var uuid: UUID? = null

    @Column(name = "content", nullable = false)
    var content: String? = null

    fun toDto(): MessageDto {

        val from = if (owner === "CLIENT") client!!.uuid else coach!!.uuid
        val to = if (owner === "CLIENT") coach!!.uuid else client!!.uuid

        return MessageDto(
            id = id,
            identifier = uuid!!,
            message = content!!,
            from = from,
            to = to,
            owner = MessageDto.Owner.valueOf(owner!!),
            time = time!!
        )
    }

    companion object {
        fun MessageDto.toMessage(coach: Coach, client: Client): Message {

            val message = Message()
            message.id = this.id
            message.content = this.message
            message.time = this.time
            message.owner = this.owner.name
            message.uuid = this.identifier
            message.coach = coach
            message.client = client

            return message
        }
    }

}