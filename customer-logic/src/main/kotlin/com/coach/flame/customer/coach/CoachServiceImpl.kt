package com.coach.flame.customer.coach

import com.coach.flame.customer.CustomerService
import com.coach.flame.customer.client.ClientService
import com.coach.flame.domain.ClientStatusDto
import com.coach.flame.domain.CoachDto
import com.coach.flame.domain.CustomerTypeDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class CoachServiceImpl(
    private val customerService: CustomerService,
    private val clientService: ClientService,
) : CoachService {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(CoachServiceImpl::class.java)
    }

    override fun getCoachWithClientsAccepted(uuid: UUID): CoachDto {

        val coach = customerService.getCustomer(uuid, CustomerTypeDto.COACH) as CoachDto

        LOGGER.info("opr='getCoachWithClientsAvailable', msg='Filter clients'")

        return coach.copy(
            listOfClients = coach.listOfClients
                .filter { ClientStatusDto.ACCEPTED == it.clientStatus }
                .toSet()
        )

    }

    override fun getCoachWithClientsAvailable(uuid: UUID): CoachDto {

        val coach = customerService.getCustomer(uuid, CustomerTypeDto.COACH) as CoachDto

        val clientsForCoach = clientService.getAllClientsForCoach(uuid)

        LOGGER.info("opr='getCoachWithClientsAvailable', msg='Number of clients for this coach', size={}",
            clientsForCoach.size)

        return coach.copy(listOfClients = clientsForCoach)
    }

}