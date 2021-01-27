package com.coach.flame.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.PropertySources
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackages = ["com.coach.flame.repo"])
@EntityScan(basePackages = ["com.coach.flame.repo.entity"])
@PropertySources(
    PropertySource("classpath:application-repository.properties")
)
open class FlameCoachRepoConfig