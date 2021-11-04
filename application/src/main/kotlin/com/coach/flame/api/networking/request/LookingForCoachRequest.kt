package com.coach.flame.api.networking.request

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class LookingForCoachRequest {

    @field:NotBlank
    @field:NotNull
    var identifier: String? = null

    override fun toString(): String {
        return "LookingForCoachRequest(" +
                "identifier=$identifier" +
                ")"
    }

}
