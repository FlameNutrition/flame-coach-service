package com.coach.flame.customer.coach

import com.coach.flame.customer.CustomerService
import com.coach.flame.customer.CustomerServiceImpl
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
) : CoachService {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(CoachServiceImpl::class.java)
    }

    override fun getCoachWithClientsAvailable(uuid: UUID): CoachDto {

        val coach = customerService.getCustomer(uuid, CustomerTypeDto.COACH) as CoachDto

        LOGGER.info("opr='getCoachWithClientsAvailable', msg='Filter clients'")

        return coach.copy(
            listOfClients = coach.listOfClients
                .filter { ClientStatusDto.WITH_COACH == it.clientStatus }
                .toSet()
        )

    }

}