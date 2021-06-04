package com.coach.flame.testing.component.base.mock

import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestComponent
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender

@TestComponent
class MockJavaMailSender {

    @Autowired
    private lateinit var emailSender: JavaMailSender

    fun sendEmail(): CapturingSlot<SimpleMailMessage> {
        val emailCapture = slot<SimpleMailMessage>()

        every {
            emailSender.send(capture(emailCapture))
        } returns mockk()

        return emailCapture
    }

    fun sendEmail(exception: RuntimeException): CapturingSlot<SimpleMailMessage> {
        val emailCapture = slot<SimpleMailMessage>()

        every {
            emailSender.send(capture(emailCapture))
        } throws exception

        return emailCapture
    }

}
