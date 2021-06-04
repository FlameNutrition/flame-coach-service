package com.coach.flame.api.client.response

import java.util.*

data class ClientInviteResponse(
    val coachIdentifier: UUID,
    val registrationLink: String,
    val registrationKey: String,
)
