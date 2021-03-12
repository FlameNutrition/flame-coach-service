package com.coach.flame.api.coach.response

import java.util.*

data class CoachResponse(
    val identifier: UUID,
    val clientsCoach: Set<ClientCoach> = setOf()
)