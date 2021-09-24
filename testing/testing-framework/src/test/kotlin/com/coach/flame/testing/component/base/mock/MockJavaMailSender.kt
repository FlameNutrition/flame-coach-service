package com.coach.flame.testing.component.base.mock

import io.mockk.CapturingSlot
import io.mockk.MockKStubScope
import io.mockk.every
import io.mockk.slot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestComponent
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender

@TestComponent
class MockJavaMailSender : MockRepository<MockJavaMailSender, SimpleMailMessage>() {

    companion object {
        const val SEND = "send"
        const val SEND_THROW_EXCEPTION = "send-throw-exception"
    }

    @Autowired
    private lateinit var emailSender: JavaMailSender

    private fun sendEmail(): Pair<MockKStubScope<Any?, Any?>, CapturingSlot<SimpleMailMessage>> {
        val emailCapture = slot<SimpleMailMessage>()
        return Pair(every { emailSender.send(capture(emailCapture)) }, emailCapture)
    }

    override fun returnsBool(f: () -> Boolean) {
        throw UnsupportedOperationException("returnsBool doest not have any method implemented!")
    }

    override fun returnsMulti(f: () -> List<SimpleMailMessage?>) {
        throw UnsupportedOperationException("returnsMulti doest not have any method implemented!")
    }

    override fun returns(f: () -> SimpleMailMessage?) {
        throw UnsupportedOperationException("returns doest not have any method implemented!")
    }

    override fun capture(): CapturingSlot<SimpleMailMessage> {
        return when (mockMethod) {
            SEND -> {
                val result = sendEmail()
                result.first returns result.second
                result.second
            }
            SEND_THROW_EXCEPTION -> {
                val result = sendEmail()
                result.first throws RuntimeException("Something is wrong!")
                result.second
            }
            else -> throw RuntimeException("Missing mock method name!")
        }
    }

}
