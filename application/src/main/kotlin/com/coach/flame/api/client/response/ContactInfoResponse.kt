package com.coach.flame.api.client.response

import java.util.*

data class ContactInfoResponse(
    val identifier: UUID,
    val firstName: String,
    val lastName: String,
    val phoneCode: String?,
    val phoneNumber: String?,
    val country: Config?,
)
