package com.coach.flame.api.networking.request

class MessageRequest(
    val from: String,
    val to: String,
    val message: String,
    val owner: Owner
) {

    enum class Owner {
        CLIENT, COACH
    }

    override fun toString(): String {
        return "MessageRequest(" +
                "from=$from, " +
                "to=$to, " +
                "owner=$owner, " +
                "message=$message" +
                ")"
    }

}


