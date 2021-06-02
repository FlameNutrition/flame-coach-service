package com.coach.flame.customer

import com.coach.flame.FlameCoachRepoConfig
import com.coach.flame.customer.props.PropsCredentials
import com.coach.flame.customer.security.HashPassword
import com.coach.flame.customer.security.Salt
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.*

@Configuration
@ComponentScan(value = [
    "com.coach.flame.customer",
    "com.coach.flame.customer.coach",
    "com.coach.flame.customer.client",])
@Import(value = [FlameCoachRepoConfig::class])
@PropertySources(
    PropertySource("classpath:application-credentials.properties")
)
@ConfigurationPropertiesScan("com.coach.flame.customer.props")
class CustomerModuleConfig(
    private val propsCredentials: PropsCredentials
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

}
