package com.coach.flame.api.coach

import com.coach.flame.api.coach.request.ContactInfoRequest
import com.coach.flame.api.coach.response.CoachResponse
import com.coach.flame.api.coach.response.ContactInfoResponse
import java.util.*

interface CoachApi {

    fun getClientsCoach(identifier: String): CoachResponse

    fun getClientsCoachPlusClientsAvailable(identifier: String): CoachResponse

    fun getContactInformation(identifier: UUID): ContactInfoResponse

    fun updateContactInformation(identifier: UUID, request: ContactInfoRequest): ContactInfoResponse
}
