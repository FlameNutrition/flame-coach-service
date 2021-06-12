package com.coach.flame.api.client.response

import java.util.*

data class ClientInviteResponse(
    val coachIdentifier: UUID,
    val registrationInvite: Boolean = false,
    val registrationLink: String?,
    val registrationKey: String?,
    val clientStatus: String?
)
