package com.coach.flame.domain

import java.util.*

data class MessageDto(
    val identifier: UUID? = null,
    val message: String,
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
                "message='$message', " +
                "owner=$owner" +
                ")"
    }
}
