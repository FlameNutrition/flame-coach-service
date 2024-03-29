package com.coach.flame.api.client

import com.coach.flame.api.client.request.EnrollmentRequest
import com.coach.flame.api.client.response.EnrollmentResponse
import java.util.*

interface EnrollmentCoachApi {

    fun init(enrollmentRequest: EnrollmentRequest): EnrollmentResponse

    fun finish(enrollmentRequest: EnrollmentRequest): EnrollmentResponse

    fun `break`(enrollmentRequest: EnrollmentRequest): EnrollmentResponse

    fun status(clientUUID: UUID): EnrollmentResponse
}
