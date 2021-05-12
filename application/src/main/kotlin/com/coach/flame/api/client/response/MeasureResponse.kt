package com.coach.flame.api.client.response

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class MeasureResponse(
    val weights: List<Measure>?,
)
