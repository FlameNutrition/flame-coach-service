package com.coach.flame.api.coach

import com.coach.flame.api.coach.response.CoachResponse

interface CoachApi {

    fun getClientsCoach(identifier: String): CoachResponse

    fun getClientsCoachPlusClientsAvailable(identifier: String): CoachResponse
}