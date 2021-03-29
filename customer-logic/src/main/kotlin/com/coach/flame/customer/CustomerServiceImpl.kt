package com.coach.flame.customer

import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.CoachDto
import com.coach.flame.domain.Customer
import com.coach.flame.domain.CustomerTypeDto
import com.coach.flame.domain.converters.ClientDtoConverter
import com.coach.flame.domain.converters.CoachDtoConverter
import com.coach.flame.jpa.entity.*
import com.coach.flame.jpa.repository.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class CustomerServiceImpl(
    private val clientRepository: ClientRepository,
    private val coachRepository: CoachRepository,
    private val clientTypeRepository: ClientTypeRepository,
    private val userSessionRepository: UserSessionRepository,
    private val userRepository: UserRepository,
    private val clientDtoConverter: ClientDtoConverter,
    private val coachDtoConverter: CoachDtoConverter,
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

                return clientDtoConverter.convert(client)
            }
            CustomerTypeDto.COACH -> {
                val coach =
                    coachRepository.findByUuid(uuid)
                        ?: throw CustomerNotFoundException("Could not found any coach with uuid: $uuid")

                return coachDtoConverter.convert(coach)

            }
            else -> throw CustomerRetrieveException("$customerType is an invalid customer type")
        }
    }

    @Transactional
    override fun updateCustomer(uuid: UUID, customer: Customer): Customer {

        LOGGER.info("opr='updateCustomer', msg='Update Customer', uuid={}, type={}", uuid, customer.customerType)

        when (customer.customerType) {
            CustomerTypeDto.CLIENT -> {
                val clientDto = customer as ClientDto
                val client = clientRepository.findByUuid(uuid)
                    ?: throw CustomerNotFoundException("Could not found any client with uuid: $uuid")

                client.firstName = clientDto.firstName
                client.lastName = clientDto.lastName
                client.birthday = clientDto.birthday
                client.phoneCode = clientDto.phoneCode
                client.phoneNumber = clientDto.phoneNumber
                client.country = clientDto.country?.let { CountryConfig(it.countryCode, it.externalValue) }
                client.gender = clientDto.gender?.let { GenderConfig(it.genderCode, it.externalValue) }
                client.measureConfig = MeasureConfig.valueOf(clientDto.measureType.code)
                client.weight = clientDto.weight
                client.height = clientDto.height

                val newClient = clientRepository.save(client)

                return clientDtoConverter.convert(newClient)
            }
            CustomerTypeDto.COACH -> {
                val coachDto = customer as CoachDto
                val coach = coachRepository.findByUuid(uuid)
                    ?: throw CustomerNotFoundException("Could not found any coach with uuid: $uuid")

                coach.firstName = coachDto.firstName
                coach.lastName = coachDto.lastName
                coach.birthday = coachDto.birthday
                coach.phoneCode = coachDto.phoneCode
                coach.phoneNumber = coachDto.phoneNumber
                coach.country = coachDto.country?.let { CountryConfig(it.countryCode, it.externalValue) }
                coach.gender = coachDto.gender?.let { GenderConfig(it.genderCode, it.externalValue) }

                val newCoach = coachRepository.save(coach)

                return coachDtoConverter.convert(newCoach)

            }
            else -> throw CustomerRetrieveException("${customer.customerType} is an invalid customer type")
        }
    }

    @Transactional
    override fun registerCustomer(customer: Customer): Customer {

        LOGGER.info("opr='registerCustomer', msg='Register a new client'")

        try {

            checkNotNull(customer.loginInfo) { "loginInfo is a mandatory parameter" }

            val clientType = clientTypeRepository.getByType(customer.customerType.name)

            val expirationDate = LocalDateTime.now().plusHours(2)

            val user = User(
                email = customer.loginInfo!!.username,
                //TODO: Encrypt password
                password = customer.loginInfo!!.password,
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
                    val client = clientRepository.save(entity)
                    return clientDtoConverter.convert(client)
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
                    val client = coachRepository.save(entity)
                    return coachDtoConverter.convert(client)
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

        if (user.client !== null || user.coach !== null) {
            LOGGER.info("opr='getNewCustomerSession', msg='Update the session'")

            // Set the expiration date with more 2 hours
            val expirationDate = LocalDateTime.now().plusHours(2)

            LOGGER.info("opr='getNewCustomerSession', msg='Update expiration date', expirationDate={}", expirationDate)

            user.userSession.expirationDate = expirationDate
        }

        return when {
            user.client !== null -> {
                userSessionRepository.save(user.client!!.user.userSession)
                clientDtoConverter.convert(user.client!!)
            }
            user.coach !== null -> {
                userSessionRepository.save(user.coach!!.user.userSession)
                coachDtoConverter.convert(user.coach!!)
            }
            else -> {
                LOGGER.error("opr='getNewCustomerSession', " +
                        "msg='Please check coach or client for the following user.', username={}", username)
                throw RuntimeException("Something is wrong with jpa")
            }
        }
    }


}
