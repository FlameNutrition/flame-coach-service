package com.coach.flame.domain

import java.util.*

data class InviteInfoDto(
    val sender: UUID,
    val isRegistrationInvite: Boolean,
) {
    var registrationLink: String? = null
    var registrationKey: String? = null
    var clientStatus: ClientStatusDto? = null
}
