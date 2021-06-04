package com.coach.flame.customer.email


import com.coach.flame.base64.Base64
import com.coach.flame.customer.props.PropsApplication
import com.coach.flame.date.DateHelper
import com.coach.flame.domain.CoachDto
import com.coach.flame.domain.RegistrationInviteDto
import com.coach.flame.jpa.entity.Coach.Companion.toCoach
import com.coach.flame.jpa.entity.RegistrationInvite
import com.coach.flame.jpa.repository.RegistrationInviteRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class EmailCustomerServiceImpl(
    private val emailService: EmailService,
    private val registrationInviteRepository: RegistrationInviteRepository,
    private val propsApplication: PropsApplication,
) : EmailCustomerService {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(EmailCustomerServiceImpl::class.java)

        private const val SUBJECT: String = "Flame Coach registration link"
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

}
