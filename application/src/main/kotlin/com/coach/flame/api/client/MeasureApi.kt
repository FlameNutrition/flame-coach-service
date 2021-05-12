package com.coach.flame.api.client

import com.coach.flame.api.client.request.MeasureRequest
import com.coach.flame.api.client.response.MeasureResponse
import java.util.*

interface MeasureApi {

    fun getWeights(clientIdentifier: UUID?): MeasureResponse

    fun addWeight(request: MeasureRequest, clientIdentifier: UUID?): MeasureResponse

    fun editWeight(request: MeasureRequest, clientIdentifier: UUID?, id: Long?): MeasureResponse

    fun deleteWeight(clientIdentifier: UUID?, id: Long?): MeasureResponse

}
