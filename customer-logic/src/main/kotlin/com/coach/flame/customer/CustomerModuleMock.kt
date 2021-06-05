package com.coach.flame.customer

import com.coach.flame.customer.stub.JavaMailSenderStub
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender

@Configuration
class CustomerModuleMock {

    @Bean(name = ["emailSender"])
    @ConditionalOnProperty(name = ["flamecoach.mock.service"], havingValue = "true")
    fun getEmailSender(): JavaMailSender {
        return JavaMailSenderStub()
    }

}
