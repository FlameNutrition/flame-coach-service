package com.coach.flame.api.networking

import com.coach.flame.api.networking.request.LookingForCoachRequest
import com.coach.flame.api.networking.response.LookingForCoachResponse

/**
 * //TODO: Add description
 *
 * @author Nuno Bento
 */
interface ClientsSocialNetworkingAPI {

    /**
     * Get status looking for a coach
     *
     * @param request information to allows system get status of looking for coach for specific client
     *
     * @return looking for coach status if successfully otherwise returns an exception
     */
    fun statusLookingForCoach(request: LookingForCoachRequest): LookingForCoachResponse

    /**
     * Enable looking for a coach
     *
     * @param request information to allows system enable the looking for coach for specific client
     *
     * @return looking for coach enabled if successfully otherwise returns an exception
     */
    fun enableLookingForCoach(request: LookingForCoachRequest): LookingForCoachResponse

    /**
     * Disable looking for a coach
     *
     * @param request information to allows system disable the looking for coach for specific client
     *
     * @return looking for coach enabled if successfully otherwise returns an exception
     */
    fun disableLookingForCoach(request: LookingForCoachRequest): LookingForCoachResponse


}
