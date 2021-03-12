package com.coach.flame.api.coach

import com.coach.flame.api.coach.request.CoachRequest
import com.coach.flame.api.coach.response.CoachResponse

interface CoachApi {

    fun getClientsCoach(coachRequest: CoachRequest): CoachResponse

}