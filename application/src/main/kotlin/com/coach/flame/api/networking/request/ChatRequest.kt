package com.coach.flame.api.networking.request

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class ChatRequest {
    @field:NotNull
    @field:NotBlank
    var client: String? = null

    @field:NotNull
    @field:NotBlank
    var coach: String? = null

    override fun toString(): String {
        return "ChatRequest(" +
                "client=$client, " +
                "coach=$coach" +
                ")"
    }
}
