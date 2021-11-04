package com.coach.flame.socialnetworking

import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.LookingForCoachDto
import java.util.*

/**
 * Use this service to get/enable/disable the "looking for coach" behaviour
 *
 * @author Nuno Bento
 */
interface SocialNetworkingService {

    /**
     * Get the "looking for coach" client status
     *
     * @param client identification
     *
     * @return lookingForCoach information otherwise an exception
     */
    fun getLookingForCoachStatus(client: UUID): LookingForCoachDto

    /**
     * Enable the "looking for coach" client
     *
     * @param client identification
     *
     * @return lookingForCoach information otherwise an exception
     */
    fun enableLookingForCoach(client: UUID): LookingForCoachDto

    /**
     * Disable the "looking for coach" client
     *
     * @param client identification
     *
     * @return lookingForCoach information otherwise an exception
     */
    fun disableLookingForCoach(client: UUID): LookingForCoachDto


}
