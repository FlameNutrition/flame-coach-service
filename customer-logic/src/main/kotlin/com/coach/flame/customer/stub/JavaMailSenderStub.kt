package com.coach.flame.customer.stub

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessagePreparator
import java.io.InputStream
import javax.mail.internet.MimeMessage

class JavaMailSenderStub : JavaMailSender {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(JavaMailSenderStub::class.java)
    }

    override fun send(p0: MimeMessage) {
        TODO("Not yet implemented")
    }

    override fun send(vararg p0: MimeMessage?) {
        TODO("Not yet implemented")
    }

    override fun send(p0: MimeMessagePreparator) {
        TODO("Not yet implemented")
    }

    override fun send(vararg p0: MimeMessagePreparator?) {
        TODO("Not yet implemented")
    }

    override fun send(p0: SimpleMailMessage) {
        LOGGER.info("opr='send', msg='Using JavaMailSenderStub class'")
    }

    override fun send(vararg p0: SimpleMailMessage?) {
        TODO("Not yet implemented")
    }

    override fun createMimeMessage(): MimeMessage {
        TODO("Not yet implemented")
    }

    override fun createMimeMessage(p0: InputStream): MimeMessage {
        TODO("Not yet implemented")
    }

}
