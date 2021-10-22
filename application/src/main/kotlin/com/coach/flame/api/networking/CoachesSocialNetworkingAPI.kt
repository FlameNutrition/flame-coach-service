package com.coach.flame.api.networking

import com.coach.flame.api.networking.request.InviteClientRequest
import com.coach.flame.api.networking.response.InviteClientResponse

/**
 * //TODO: Add description
 *
 * @author Nuno Bento
 */
interface CoachesSocialNetworkingAPI {

    /**
     * Send invite to client
     *
     * @param request with necessary information to link the client and coach with
     * an invitation
     *
     * @return a valid response if invite sent with success otherwise returns an exception
     */
    fun inviteClient(request: InviteClientRequest): InviteClientResponse

}
