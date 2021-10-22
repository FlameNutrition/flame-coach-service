package com.coach.flame.api.networking

import com.coach.flame.api.networking.request.LookingForCoachRequest
import com.coach.flame.api.networking.response.LookingForCoachResponse
import com.coach.flame.aspect.LoggingRequest
import com.coach.flame.aspect.LoggingResponse
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/socialNetworking/client")
class ClientsSocialNetworkingImpl : ClientsSocialNetworkingAPI {

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/enableLookingForCoach")
    @ResponseBody
    override fun enableLookingForCoach(
        @Valid request: LookingForCoachRequest
    ): LookingForCoachResponse {
        TODO("Not yet implemented")
    }

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/disableLookingForCoach")
    @ResponseBody
    override fun disableLookingForCoach(
        @Valid request: LookingForCoachRequest
    ): LookingForCoachResponse {
        TODO("Not yet implemented")
    }


}
