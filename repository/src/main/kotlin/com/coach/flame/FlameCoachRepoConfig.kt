package com.coach.flame

import com.coach.flame.jpa.entity.ClientType
import com.coach.flame.jpa.entity.CountryConfig
import com.coach.flame.jpa.entity.GenderConfig
import com.coach.flame.jpa.repository.cache.ConfigCache
import com.coach.flame.jpa.repository.configs.CountryConfigRepository
import com.coach.flame.jpa.repository.configs.CustomerTypeRepository
import com.coach.flame.jpa.repository.configs.GenderConfigRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.*
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.concurrent.TimeUnit

@SpringBootConfiguration
@EnableJpaRepositories(basePackages = [
    "com.coach.flame.jpa.repository",
], entityManagerFactoryRef = "entityManagerFactory")
@ComponentScan("com.coach.flame.jpa.repository.operations")
@EntityScan(basePackages = ["com.coach.flame.jpa.entity"])
@EnableTransactionManagement
@PropertySources(
    PropertySource("classpath:application-repository.properties")
)
@Import(value = [FlameCoachRepoConfigAutomationTesting::class])
class FlameCoachRepoConfig {

    @Autowired
    private lateinit var customerTypeRepository: CustomerTypeRepository

    @Autowired
    private lateinit var countryConfigRepository: CountryConfigRepository

    @Autowired
    private lateinit var genderConfigRepository: GenderConfigRepository

    @Bean
    fun customerTypeCache(): ConfigCache<ClientType> {
        return ConfigCache(1, TimeUnit.DAYS, customerTypeRepository)
    }

    @Bean
    fun countryConfigCache(): ConfigCache<CountryConfig> {
        return ConfigCache(1, TimeUnit.DAYS, countryConfigRepository)
    }

    @Bean
    fun genderConfigCache(): ConfigCache<GenderConfig> {
        return ConfigCache(1, TimeUnit.DAYS, genderConfigRepository)
    }

}
