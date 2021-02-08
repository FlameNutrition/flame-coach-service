package com.coach.flame.api.response

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.Objects.hash

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DailyTask(
    val identifier: String? = null,
    val name: String? = null,
    val description: String? = null,
    val date: String? = null,
    val ticked: Boolean? = null
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DailyTask

        if (identifier != other.identifier) return false

        return true
    }

    override fun hashCode(): Int {
        return hash(identifier)
    }
}
