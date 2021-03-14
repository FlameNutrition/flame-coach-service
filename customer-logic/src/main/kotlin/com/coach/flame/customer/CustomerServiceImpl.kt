package com.coach.flame.customer

import com.coach.flame.domain.CustomerTypeDto
import com.coach.flame.domain.Customer
import com.coach.flame.domain.converters.ClientToClientDtoConverter
import com.coach.flame.domain.converters.CoachToCoachDtoConverter
import com.coach.flame.jpa.entity.*
import com.coach.flame.jpa.repository.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Service
class CustomerServiceImpl(
    private val clientRepository: ClientRepository,
    private val coachRepository: CoachRepository,
    private val clientTypeRepository: ClientTypeRepository,
    private val userSessionRepository: UserSessionRepository,
    private val userRepository: UserRepository,
    private val clientToClientDtoConverter: ClientToClientDtoConverter,
    private val coachToCoachDtoConverter: CoachToCoachDtoConverter,
) : CustomerService {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(CustomerServiceImpl::class.java)
    }

    @Transactional(readOnly = true)
    override fun getCustomer(uuid: UUID, customerType: CustomerTypeDto): Customer {

        LOGGER.info("opr='getCustomer', msg='Get Customer', uuid={}, type={}", uuid, customerType)

        when (customerType) {
            CustomerTypeDto.CLIENT -> {
                val client =
                    clientRepository.findByUuid(uuid)
                        ?: throw CustomerNotFoundException("Could not found any client with uuid: $uuid")

                return clientToClientDtoConverter.convert(client)
            }
            CustomerTypeDto.COACH -> {
                val coach =
                    coachRepository.findByUuid(uuid)
                        ?: throw CustomerNotFoundException("Could not found any coach with uuid: $uuid")

                return coachToCoachDtoConverter.convert(coach)

            }
            else -> throw CustomerRetrieveException("$customerType is a invalid customer type")
        }
    }

    @Transactional
    override fun registerCustomer(customer: Customer): Customer {

        LOGGER.info("opr='registerCustomer', msg='Register a new client'")

        try {

            checkNotNull(customer.customerType) { "clientType is a mandatory parameter" }
            checkNotNull(customer.firstName) { "firstName is a mandatory parameter" }
            checkNotNull(customer.lastName) { "lastName is a mandatory parameter" }
            checkNotNull(customer.loginInfo?.username) { "loginInfo->username is a mandatory parameter" }
            checkNotNull(customer.loginInfo?.password) { "loginInfo->password is a mandatory parameter" }

            val clientType = clientTypeRepository.getByType(customer.customerType.name)

            val expirationDate = LocalDateTime.now().plusHours(2)

            val user = User(
                email = customer.loginInfo?.username!!,
                //TODO: Encrypt password
                password = customer.loginInfo?.password!!,
                userSession = UserSession(
                    expirationDate = expirationDate,
                    token = UUID.randomUUID()
                )
            )

            LOGGER.info("opr='registerCustomer', msg='Customer type information', clientType={}", customer.customerType)

            when (customer.customerType) {
                CustomerTypeDto.CLIENT -> {
                    val entity = Client(
                        uuid = customer.identifier,
                        firstName = customer.firstName,
                        lastName = customer.lastName,
                        clientType = clientType,
                        user = user,
                        clientStatus = ClientStatus.AVAILABLE,
                        registrationDate = customer.registrationDate
                    )
                    val client = clientRepository.saveAndFlush(entity)
                    return clientToClientDtoConverter.convert(client)
                }
                CustomerTypeDto.COACH -> {
                    val entity = Coach(
                        uuid = customer.identifier,
                        firstName = customer.firstName,
                        lastName = customer.lastName,
                        clientType = clientType,
                        user = user,
                        registrationDate = customer.registrationDate
                    )
                    val client = coachRepository.saveAndFlush(entity)
                    return coachToCoachDtoConverter.convert(client)
                }
                else -> throw CustomerRegisterException("${customer.customerType} is a invalid customer type")
            }

        } catch (ex: Exception) {
            when (ex) {
                is DataIntegrityViolationException -> throw CustomerRegisterDuplicateException("The following customer already exists")
                else -> throw ex
            }
        }
    }

    @Transactional
    override fun getNewCustomerSession(username: String, password: String): Customer {

        LOGGER.info("opr='getNewCustomerSession', msg='Get a new customer session'")

        val user = userRepository.findUserByEmailAndPassword(username, password)
            ?: throw CustomerUsernameOrPasswordException("Username or password invalid")

        LOGGER.info("opr='getNewCustomerSession', msg='Update the session'")

        // Set the expiration date with more 2 hours
        val expirationDate = LocalDateTime.now().plusHours(2)

        LOGGER.info("opr='getNewCustomerSession', msg='Update expiration date', expirationDate={}", expirationDate)

        user.userSession.expirationDate = expirationDate

        return if (user.client !== null) {
            userSessionRepository.saveAndFlush(user.client?.user?.userSession!!)
            clientToClientDtoConverter.convert(user.client!!)
        } else {
            userSessionRepository.saveAndFlush(user.coach?.user?.userSession!!)
            coachToCoachDtoConverter.convert(user.coach!!)
        }
    }


}