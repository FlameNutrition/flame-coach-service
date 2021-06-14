package com.coach.flame.customer.register


import com.coach.flame.base64.Base64
import com.coach.flame.customer.CustomerRegisterExpirationDate
import com.coach.flame.customer.CustomerRegisterInvalidEmail
import com.coach.flame.customer.CustomerRegisterWrongRegistrationKey
import com.coach.flame.customer.email.EmailService
import com.coach.flame.customer.props.PropsApplication
import com.coach.flame.date.DateHelper
import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.CoachDto
import com.coach.flame.domain.RegistrationInviteDto
import com.coach.flame.jpa.entity.Coach.Companion.toCoach
import com.coach.flame.jpa.entity.RegistrationInvite
import com.coach.flame.jpa.repository.RegistrationInviteRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class RegistrationCustomerServiceImpl(
    private val emailService: EmailService,
    private val registrationInviteRepository: RegistrationInviteRepository,
    private val propsApplication: PropsApplication,
) : RegistrationCustomerService {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(RegistrationCustomerServiceImpl::class.java)

        private const val SUBJECT: String = "Flame Coach Registration Link"
        private const val MESSAGE_TEMPLATE: String = "Hello, " +
                "Your coach %s, would like to invite you for the Flame Coach. This is a platform will allow you and " +
                "your coach to track our progress. Good luck for this adventure.\n\n" +
                "Please use the following link to create our account: %s"
    }

    @Transactional
    override fun sendRegistrationLink(coachDto: CoachDto, clientEmail: String): RegistrationInviteDto {

        val nowDate = LocalDateTime.now()
        val expirationDateUtc = DateHelper.toUTCDate(nowDate.plusHours(2))

        val key = Base64.encode("${DateHelper.toISODate(expirationDateUtc)}_${clientEmail}")

        LOGGER.debug("opr='sendRegistrationLink', msg='Created registration key', key={}", key)

        val link = "${propsApplication.registrationLink}?registrationKey=${key}&email=${clientEmail}"

        val message = String.format(MESSAGE_TEMPLATE, coachDto.firstName, link)

        emailService.sendEmail(coachDto.loginInfo!!.username, clientEmail, SUBJECT, message)

        val registrationInvite = RegistrationInvite().apply {
            coach = coachDto.toCoach()
            sendTo = clientEmail
            registrationKey = key
            sendDttm = nowDate
        }

        val registrationInviteDto = registrationInviteRepository.save(registrationInvite).toDto(link)

        LOGGER.debug("opr='sendRegistrationLink', msg='Registration Invite sent', registrationInviteDto={}",
            registrationInviteDto)

        return registrationInviteDto

    }

    @Transactional(readOnly = true)
    override fun checkRegistrationLink(clientDto: ClientDto): Boolean {

        requireNotNull(clientDto.registrationKey) { "registrationKey can not be null" }
        requireNotNull(clientDto.loginInfo) { "loginInfo can not be null" }

        val registrationKeyDecoded = Base64.decode(clientDto.registrationKey!!)

        val keyExists = registrationInviteRepository.existsByRegistrationKeyIs(clientDto.registrationKey!!)

        if (!keyExists) {
            throw CustomerRegisterWrongRegistrationKey("Registration key invalid")
        }

        val keySplit = registrationKeyDecoded.split("_")

        val expirationDate = LocalDateTime.parse(keySplit[0])

        if (LocalDateTime.now().isAfter(expirationDate)) {
            throw CustomerRegisterExpirationDate("Registration key expired")
        }

        if (!clientDto.loginInfo?.username.equals(keySplit[1])) {
            throw CustomerRegisterInvalidEmail("Invalid email, use the email received the registration link")
        }

        return true

    }

    @Transactional
    override fun updateRegistration(clientDto: ClientDto): RegistrationInviteDto {

        requireNotNull(clientDto.registrationKey) { "registrationKey can not be null" }
        requireNotNull(clientDto.loginInfo) { "loginInfo can not be null" }

        val link = propsApplication.registrationLink +
                "?registrationKey=${clientDto.registrationKey}&email=${clientDto.loginInfo!!.username}"

        val registrationInvite = registrationInviteRepository.findByRegistrationKeyIs(clientDto.registrationKey!!)
            ?: throw CustomerRegisterWrongRegistrationKey("Could not found any registration invite")

        registrationInvite.apply {
            acceptedDttm = LocalDateTime.now()
        }

        return registrationInviteRepository.save(registrationInvite).toDto(link)

    }


}
