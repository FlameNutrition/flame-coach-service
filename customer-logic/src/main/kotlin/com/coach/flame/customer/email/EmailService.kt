package com.coach.flame.customer.email

import com.coach.flame.customer.MailException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
class EmailService(
    private val emailSender: JavaMailSender,
) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(EmailService::class.java)
    }

    fun sendEmail(from: String?, to: String, subject: String, message: String) {

        try {
            val mailMessage = SimpleMailMessage()

            from?.let { mailMessage.setFrom(it) }
            mailMessage.setTo(to)
            mailMessage.setSubject(subject)
            mailMessage.setText(message)

            LOGGER.debug("opr='sendEmail', msg='Sending email.', from={}, to={}, subject='{}', message='{}'",
                from, to, subject, message)

            emailSender.send(mailMessage)
        } catch (ex: Exception) {
            LOGGER.error("opr='sendEmail', msg='Problem trying to send an email'", ex)
            throw MailException("Something happened trying to send registration link email",
                ex)
        }
    }

}
