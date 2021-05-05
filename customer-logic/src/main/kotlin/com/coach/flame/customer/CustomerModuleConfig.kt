package com.coach.flame.customer

import com.coach.flame.FlameCoachRepoConfig
import com.coach.flame.customer.security.HashPassword
import com.coach.flame.customer.security.Salt
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@ComponentScan(value = [
    "com.coach.flame.customer",
    "com.coach.flame.customer.coach",
    "com.coach.flame.customer.client"])
@Import(value = [FlameCoachRepoConfig::class])
class CustomerModuleConfig(
    @Value(value = "\${flamecoach.security.salt.length}")
    private val saltLength: Int,

    @Value(value = "\${flamecoach.security.hashing.algorithm}")
    private val algorithm: String,

    @Value(value = "\${flamecoach.security.hashing.iterations}")
    private val iterations: Int,

    @Value(value = "\${flamecoach.security.hashing.lengthKey}")
    private val lengthKey: Int,
) {

    @Bean(name = ["saltTool"])
    fun getSalt(): Salt {
        return Salt(saltLength)
    }

    @Bean(name = ["hashPasswordTool"])
    fun getHashPassword(): HashPassword {
        return HashPassword(algorithm, iterations, lengthKey)
    }
}
