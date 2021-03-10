package com.coach.flame

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.PropertySources
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootConfiguration
@EnableJpaRepositories(basePackages = ["com.coach.flame.jpa.repository"], entityManagerFactoryRef = "entityManagerFactory")
@EntityScan(basePackages = ["com.coach.flame.jpa.entity"])
@EnableTransactionManagement
@PropertySources(
    PropertySource("classpath:application-repository.properties")
)
class FlameCoachRepoConfig