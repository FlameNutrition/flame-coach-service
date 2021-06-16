package com.coach.flame.customer

import com.coach.flame.customer.client.ClientEnrollmentProcess
import com.coach.flame.customer.register.RegistrationCustomerService
import com.coach.flame.customer.security.HashPassword
import com.coach.flame.customer.security.Salt
import com.coach.flame.domain.*
import com.coach.flame.failure.domain.ErrorCode
import com.coach.flame.jpa.entity.*
import com.coach.flame.jpa.repository.*
import com.coach.flame.jpa.repository.cache.ConfigCache
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
    private val registrationCustomerService: RegistrationCustomerService,
    private val clientEnrollmentProcess: ClientEnrollmentProcess,
    private val countryConfigCache: ConfigCache<CountryConfig>,
    private val genderConfigCache: ConfigCache<GenderConfig>,
    private val hashPasswordTool: HashPassword,
    private val saltTool: Salt,
) : CustomerService {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(CustomerServiceImpl::class.java)
    }

    @Transactional(readOnly = true)
    override fun getCustomer(uuid: UUID, customerType: CustomerTypeDto): Customer {

        LOGGER.info("opr='getCustomer', msg='Get Customer', uuid={}, type={}", uuid, customerType)

        return when (customerType) {
            CustomerTypeDto.CLIENT -> {
                val client = getClient(uuid)

                client.toDto(client.coach?.toDto())
            }
            CustomerTypeDto.COACH -> {
                val coach = getCoach(uuid)

                coach.toDto()

            }
            else -> throw CustomerException(ErrorCode.CODE_2004, "$customerType is an invalid customer type.")
        }
    }

    @Transactional
    override fun updateCustomer(uuid: UUID, customer: Customer): Customer {

        LOGGER.info("opr='updateCustomer', msg='Update Customer', uuid={}, type={}", uuid, customer.customerType)

        when (customer.customerType) {
            CustomerTypeDto.CLIENT -> {
                val clientDto = customer as ClientDto
                val client = getClient(uuid)

                client.firstName = clientDto.firstName
                client.lastName = clientDto.lastName
                client.birthday = clientDto.birthday
                client.phoneCode = clientDto.phoneCode
                client.phoneNumber = clientDto.phoneNumber
                client.country = getCountryConfigFromCache(clientDto.country)
                client.gender = getGenderConfigFromCache(clientDto.gender)
                client.measureConfig = MeasureConfig.valueOf(clientDto.measureType.code)
                client.weight = clientDto.weight
                client.height = clientDto.height

                val newClient = clientRepository.save(client)

                return newClient.toDto(newClient.coach?.toDto())
            }
            CustomerTypeDto.COACH -> {
                val coachDto = customer as CoachDto
                val coach = getCoach(uuid)

                coach.firstName = coachDto.firstName
                coach.lastName = coachDto.lastName
                coach.birthday = coachDto.birthday
                coach.phoneCode = coachDto.phoneCode
                coach.phoneNumber = coachDto.phoneNumber
                coach.country = getCountryConfigFromCache(coachDto.country)
                coach.gender = getGenderConfigFromCache(coachDto.gender)

                val newCoach = coachRepository.save(coach)

                return newCoach.toDto()

            }
            else -> throw CustomerException(ErrorCode.CODE_2004, "${customer.customerType} is an invalid customer type.")
        }
    }

    @Transactional
    override fun registerCustomer(customer: Customer): Customer {

        LOGGER.info("opr='registerCustomer', msg='Register a new client'")

        try {

            checkNotNull(customer.loginInfo) { "loginInfo is a mandatory parameter" }

            val clientType = clientTypeRepository.getByType(customer.customerType.name)
            val keyDecrypt = saltTool.generate()

            val expirationDate = LocalDateTime.now().plusHours(2)
            val user = User(
                email = customer.loginInfo!!.username,
                keyDecrypt = keyDecrypt,
                password = hashPasswordTool.generate(customer.loginInfo!!.password, keyDecrypt),
                userSession = UserSession(
                    expirationDate = expirationDate,
                    token = UUID.randomUUID()
                )
            )

            LOGGER.info("opr='registerCustomer', msg='Customer type information', clientType={}", customer.customerType)

            when (customer.customerType) {
                CustomerTypeDto.CLIENT -> {

                    if (registrationCustomerService.checkRegistrationLink(customer as ClientDto)) {
                        LOGGER.info("opr='registerCustomer', msg='Client with valid registration key'")
                    }

                    val entity = Client(
                        uuid = customer.identifier,
                        firstName = customer.firstName,
                        lastName = customer.lastName,
                        clientType = clientType,
                        user = user,
                        clientStatus = ClientStatus.AVAILABLE,
                        registrationDate = customer.registrationDate
                    )
                    var client = clientRepository.saveAndFlush(entity).toDto().apply {
                        registrationKey = customer.registrationKey
                    }

                    val registrationInvite = registrationCustomerService.updateRegistration(client)

                    //Start the enrollment client process
                    client = clientEnrollmentProcess.init(client, registrationInvite.sender.identifier);

                    return client
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
                    val coach = coachRepository.saveAndFlush(entity)
                    return coach.toDto()
                }
                else -> throw CustomerException(ErrorCode.CODE_2004,
                    "${customer.customerType} is a invalid customer type.")
            }

        } catch (ex: Exception) {
            when (ex) {
                is DataIntegrityViolationException -> throw CustomerRegisterDuplicateException("The following customer already exists.")
                else -> throw ex
            }
        }
    }

    @Transactional
    override fun getNewCustomerSession(username: String, password: String): Customer {

        LOGGER.info("opr='getNewCustomerSession', msg='Get a new customer session'")

        val user = getUserCheckingUsernameAndPassword(username, password)

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
                user.client!!.toDto(user.client!!.coach?.toDto())
            }
            user.coach !== null -> {
                userSessionRepository.save(user.coach!!.user.userSession)
                user.coach!!.toDto()
            }
            else -> {
                LOGGER.error("opr='getNewCustomerSession', " +
                        "msg='Please check coach or client for the following user.', username={}", username)
                throw RuntimeException("Something is wrong with jpa")
            }
        }
    }

    @Transactional
    override fun updateCustomerPassword(email: String, oldPassword: String, newPassword: String) {

        LOGGER.info("opr='updateCustomerPassword', msg='Update customer password'")

        val user = getUserCheckingUsernameAndPassword(email, oldPassword)

        val keyDecrypt = saltTool.generate()

        user.keyDecrypt = keyDecrypt
        user.password = hashPasswordTool.generate(newPassword, keyDecrypt)

        userRepository.saveAndFlush(user)

        LOGGER.info("opr='updateCustomerPassword', msg='Customer password updated with success'")

    }

    private fun getUserCheckingUsernameAndPassword(email: String, password: String): User {

        val user = userRepository.findUserByEmail(email)
            ?: throw CustomerUsernameOrPasswordException("Username invalid.")

        if (!hashPasswordTool.verify(password, user.password, user.keyDecrypt)) {
            throw CustomerUsernameOrPasswordException("Password invalid.")
        } else {
            LOGGER.info("opr='checkUsernameAndPassword', msg='Username and password correct'")
        }

        return user
    }

    private fun getClient(identifier: UUID) = clientRepository.findByUuid(identifier)
        ?: throw CustomerNotFoundException("Could not find any client with uuid: $identifier.")

    private fun getCoach(identifier: UUID) = coachRepository.findByUuid(identifier)
        ?: throw CustomerNotFoundException("Could not find any coach with uuid: $identifier.")

    private fun getCountryConfigFromCache(country: CountryDto?) =
        country?.let { countryConfigCache.getValue(it.countryCode).get() }

    private fun getGenderConfigFromCache(gender: GenderDto?) =
        gender?.let { genderConfigCache.getValue(it.genderCode).get() }

}
