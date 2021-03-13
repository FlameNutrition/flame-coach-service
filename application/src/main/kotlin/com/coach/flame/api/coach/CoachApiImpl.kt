package com.coach.flame.api.coach

import com.coach.flame.api.coach.response.ClientCoach
import com.coach.flame.api.coach.response.CoachResponse
import com.coach.flame.aspect.LoggingRequest
import com.coach.flame.aspect.LoggingResponse
import com.coach.flame.customer.coach.CoachService
import com.coach.flame.domain.CoachDto
import com.coach.flame.exception.RestInvalidRequestException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/coach")
class CoachApiImpl(
    private val coachService: CoachService,
) : CoachApi {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(CoachApiImpl::class.java)

        private val converter = { coach: CoachDto ->
            CoachResponse(
                identifier = coach.identifier,
                clientsCoach = coach.listOfClients
                    .map {
                        ClientCoach(
                            firstname = it.firstName,
                            lastname = it.lastName,
                            identifier = it.identifier,
                            status = it.clientStatus?.name)
                    }.toSet()
            )
        }
    }

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/getClientsAccepted")
    @ResponseBody
    override fun getClientsCoach(@RequestParam(required = true) identifier: String): CoachResponse {

        try {
            requireNotNull(identifier) { "Missing required parameter request: identifier" }

            val coach = coachService.getCoachWithClientsAccepted(UUID.fromString(identifier))

            return converter(coach)
        } catch (ex: IllegalArgumentException) {
            LOGGER.warn("opr='getClientsCoach', msg='Invalid request'", ex)
            throw RestInvalidRequestException(ex)
        }
    }

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/getClientsCoachPlusClientsAvailable")
    @ResponseBody
    override fun getClientsCoachPlusClientsAvailable(@RequestParam(required = true) identifier: String): CoachResponse {

        try {
            requireNotNull(identifier) { "Missing required parameter request: identifier" }

            val coach = coachService.getCoachWithClientsAvailable(UUID.fromString(identifier))

            return converter(coach)
        } catch (ex: IllegalArgumentException) {
            LOGGER.warn("opr='getClientsCoach', msg='Invalid request'", ex)
            throw RestInvalidRequestException(ex)
        }

    }
}