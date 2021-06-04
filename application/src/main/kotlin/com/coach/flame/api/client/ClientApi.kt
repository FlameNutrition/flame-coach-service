package com.coach.flame.api.client

import com.coach.flame.api.client.request.ContactInfoRequest
import com.coach.flame.api.client.request.PersonalDataRequest
import com.coach.flame.api.client.response.ClientInviteResponse
import com.coach.flame.api.client.response.ContactInfoResponse
import com.coach.flame.api.client.response.PersonalDataResponse
import java.util.*

interface ClientApi {

    fun registrationInvite(coachIdentifier: UUID?, clientEmail: String?): ClientInviteResponse

    fun getContactInformation(identifier: UUID): ContactInfoResponse

    fun updateContactInformation(identifier: UUID, request: ContactInfoRequest): ContactInfoResponse

    fun getPersonalData(identifier: UUID): PersonalDataResponse

    fun updatePersonalData(identifier: UUID, request: PersonalDataRequest): PersonalDataResponse

}
