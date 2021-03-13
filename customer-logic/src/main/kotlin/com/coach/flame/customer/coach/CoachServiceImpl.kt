package com.coach.flame.customer.coach

import com.coach.flame.customer.CustomerService
import com.coach.flame.customer.client.ClientService
import com.coach.flame.domain.ClientStatusDto
import com.coach.flame.domain.CoachDto
import com.coach.flame.domain.CustomerTypeDto
import com.coach.flame.jpa.repository.ClientRepository
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

        LOGGER.info("opr='getCoachWithClientsAvailable', msg='Number of clients this coach', size={}",
            coach.listOfClients.size)

        val clientsAvailableForCoaching = clientService.getAllClientsAvailableForCoaches()

        LOGGER.info("opr='getCoachWithClientsAvailable', msg='Number of clients available for coaching', size={}",
            clientsAvailableForCoaching.size)

        val mergedClients = clientsAvailableForCoaching.plus(coach.listOfClients)

        LOGGER.info("opr='getCoachWithClientsAvailable', msg='Number of total clients', size={}", mergedClients.size)

        return coach.copy(listOfClients = mergedClients)
    }

}