package com.coach.flame.api.coach

import com.coach.flame.api.coach.request.CoachRequest
import com.coach.flame.api.coach.response.ClientCoach
import com.coach.flame.api.coach.response.CoachResponse
import com.coach.flame.aspect.LoggingRequest
import com.coach.flame.aspect.LoggingResponse
import com.coach.flame.customer.CustomerService
import com.coach.flame.customer.coach.CoachService
import com.coach.flame.domain.CustomerTypeDto
import com.coach.flame.domain.CoachDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/coach")
class CoachApiImpl(
    private val coachService: CoachService,
) : CoachApi {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(CoachApiImpl::class.java)
    }

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/getClients")
    @ResponseBody
    override fun getClientsCoach(@RequestBody(required = true) coachRequest: CoachRequest): CoachResponse {

        val coach = coachService.getCoachWithClientsAvailable(coachRequest.identifier)

        return CoachResponse(
            identifier = coach.identifier,
            clientsCoach = coach.listOfClients
                .map {
                    ClientCoach(
                        firstname = it.firstName,
                        lastname = it.lastName,
                        identifier = it.identifier)
                }.toSet()
        )

    }
}