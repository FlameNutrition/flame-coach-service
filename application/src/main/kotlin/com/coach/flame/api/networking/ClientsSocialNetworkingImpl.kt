package com.coach.flame.api.networking

import com.coach.flame.api.APIWrapperException
import com.coach.flame.api.networking.request.LookingForCoachRequest
import com.coach.flame.api.networking.response.LookingForCoachResponse
import com.coach.flame.aspect.LoggingRequest
import com.coach.flame.aspect.LoggingResponse
import com.coach.flame.socialnetworking.SocialNetworkingService
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/socialNetworking/client")
class ClientsSocialNetworkingImpl(
    private val socialNetworkingService: SocialNetworkingService
) : ClientsSocialNetworkingAPI {

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/statusLookingForCoach")
    @ResponseBody
    override fun statusLookingForCoach(
        @Valid request: LookingForCoachRequest
    ): LookingForCoachResponse {
        return APIWrapperException.executeRequest {
            val clientUUID = UUID.fromString(request.identifier)

            val lookingForCoachDto = socialNetworkingService.getLookingForCoachStatus(clientUUID)

            LookingForCoachResponse(
                identifier = clientUUID.toString(),
                isEnable = lookingForCoachDto.isEnable,
                description = lookingForCoachDto.description ?: ""
            )
        }
    }

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
