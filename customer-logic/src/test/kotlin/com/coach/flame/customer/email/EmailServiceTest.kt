package com.coach.flame.customer.email

import com.coach.flame.customer.MailException
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender

@ExtendWith(MockKExtension::class)
class EmailServiceTest {

    @MockK
    private lateinit var javaMailSender: JavaMailSender

    @InjectMockKs
    private lateinit var classToTest: EmailService

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test send an email using java mail sender`() {

        val to = "client@test.com"
        val from = "coach@test.com"
        val subject = "This is a test email"
        val message = "This is a simple message with some special characters: \n\t@#[]!"

        val simpleMessageMail = slot<SimpleMailMessage>()

        every { javaMailSender.send(capture(simpleMessageMail)) } answers { simpleMessageMail.captured }

        classToTest.sendEmail(from, to, subject, message)

        verify(exactly = 1) { javaMailSender.send(simpleMessageMail.captured) }

        then(simpleMessageMail.captured.to).isEqualTo(Array(1) { "client@test.com" })
        then(simpleMessageMail.captured.from).isEqualTo("coach@test.com")
        then(simpleMessageMail.captured.subject).isEqualTo("This is a test email")
        then(simpleMessageMail.captured.text).isEqualTo("This is a simple message with some special characters: \n\t@#[]!")

    }

    @Test
    fun `test send an email using java mail sender but throws exception`() {

        val to = "client@test.com"
        val from = "coach@test.com"
        val subject = "This is a test email"
        val message = "This is a simple message with some special characters: \n\t@#[]!"

        every { javaMailSender.send(any<SimpleMailMessage>()) } throws RuntimeException("Email error!")

        val result = catchThrowable { classToTest.sendEmail(from, to, subject, message) }

        then(result)
            .isInstanceOf(MailException::class.java)
            .hasMessageContaining("Something happened when trying to send the registration link")
    }

    @Test
    fun `test send an email using java mail sender without from field`() {

        val to = "client@test.com"
        val subject = "This is a test email"
        val message = "This is a simple message with some special characters: \n\t@#[]!"

        val simpleMessageMail = slot<SimpleMailMessage>()

        every { javaMailSender.send(capture(simpleMessageMail)) } answers { simpleMessageMail.captured }

        classToTest.sendEmail(null, to, subject, message)

        verify(exactly = 1) { javaMailSender.send(simpleMessageMail.captured) }

        then(simpleMessageMail.captured.to).isEqualTo(Array(1) { "client@test.com" })
        then(simpleMessageMail.captured.from).isNull()
        then(simpleMessageMail.captured.subject).isEqualTo("This is a test email")
        then(simpleMessageMail.captured.text).isEqualTo("This is a simple message with some special characters: \n\t@#[]!")
    }

}
