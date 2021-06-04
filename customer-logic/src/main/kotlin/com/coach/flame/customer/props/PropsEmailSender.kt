package com.coach.flame.customer.props

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "spring.mail")
@ConstructorBinding
data class PropsEmailSender(
    val host: String,
    val port: Int,
    val username: String,
    val password: String,
    val properties: Properties,
)

data class Properties(
    val smtpAuth: Boolean,
    val starttlsEnable: Boolean,
    val debug: Boolean,
    val protocol: String,
)
