package com.coach.flame.api.networking.request

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class MessageRequest(

    @field:NotBlank
    @field:NotNull
    val from: String,

    @field:NotBlank
    @field:NotNull
    val to: String,

    @field:NotBlank
    @field:NotNull
    val message: String,

    @field:NotNull
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


