package com.coach.flame.domain

import java.time.LocalDateTime
import java.util.*

data class MessageDto(
    val id: Long? = null,
    val identifier: UUID,
    val message: String,
    val time: LocalDateTime,
    val to: UUID,
    val from: UUID,
    val owner: Owner
) {

    enum class Owner {
        CLIENT, COACH
    }

    override fun toString(): String {
        return "MessageDto(" +
                "identifier=$identifier, " +
                "to=$to, " +
                "from=$from, " +
                "time=$time, " +
                "message='$message', " +
                "owner=$owner" +
                ")"
    }
}
