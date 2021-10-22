package com.coach.flame.api.networking

import com.coach.flame.api.networking.request.InviteClientRequest
import com.coach.flame.api.networking.response.InviteClientResponse
import com.coach.flame.aspect.LoggingRequest
import com.coach.flame.aspect.LoggingResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/api/socialNetworking/coach")
class CoachesSocialNetworkingImpl : CoachesSocialNetworkingAPI {

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/inviteClient")
    @ResponseBody
    override fun inviteClient(
        @Valid request: InviteClientRequest
    ): InviteClientResponse {
        TODO("Not yet implemented")
    }

}
