package com.coach.flame.customer.props

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "flamecoach.security.hashing")
@ConstructorBinding
data class PropsCredentials(
    val saltLength: Int,
    val algorithm: String,
    val iterations: Int,
    val lengthKey: Int,
)
