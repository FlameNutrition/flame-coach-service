package com.coach.flame.customer.props

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "flamecoach.app")
@ConstructorBinding
data class PropsApplication(
    val registrationLink: String,
)
