package com.coach.flame.customer

import com.coach.flame.FlameCoachRepoConfig
import com.coach.flame.customer.props.PropsCredentials
import com.coach.flame.customer.props.PropsEmailSender
import com.coach.flame.customer.security.HashPassword
import com.coach.flame.customer.security.Salt
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.*
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*

@Configuration
@ComponentScan(value = [
    "com.coach.flame.customer",
    "com.coach.flame.customer.coach",
    "com.coach.flame.customer.client",
])
@Import(value = [FlameCoachRepoConfig::class])
@PropertySources(value = [
    PropertySource("classpath:application-credentials.properties"),
    PropertySource("classpath:application-email.properties"),
    PropertySource("classpath:customer.properties"),
])
@ConfigurationPropertiesScan("com.coach.flame.customer.props")
class CustomerModuleConfig(
    private val propsCredentials: PropsCredentials,
    private val propsEmailSender: PropsEmailSender,
) {

    @Bean(name = ["saltTool"])
    fun getSalt(): Salt {
        return Salt(propsCredentials.saltLength)
    }

    @Bean(name = ["hashPasswordTool"])
    fun getHashPassword(): HashPassword {
        return HashPassword(
            algorithm = propsCredentials.algorithm,
            iterations = propsCredentials.iterations,
            lengthKey = propsCredentials.lengthKey)
    }

    @Bean(name = ["emailSender"])
    fun getEmailSender(): JavaMailSender {
        return JavaMailSenderImpl().apply {
            host = propsEmailSender.host
            port = propsEmailSender.port
            username = propsEmailSender.username
            password = propsEmailSender.password
            javaMailProperties = Properties().apply {
                putAll(mapOf(
                    Pair("mail.transport.protocol", propsEmailSender.properties.protocol),
                    Pair("mail.smtp.auth", propsEmailSender.properties.smtpAuth),
                    Pair("mail.smtp.starttls.enable", propsEmailSender.properties.starttlsEnable),
                    Pair("mail.debug", propsEmailSender.properties.debug)))
            }

        }
    }

}
