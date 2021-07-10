package com.coach.flame

import org.springframework.boot.SpringBootConfiguration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.PropertySources

@SpringBootConfiguration
@PropertySources(
    PropertySource("classpath:application-repository-automation-testing.properties")
)
@Profile("automation-testing")
class FlameCoachRepoConfigAutomationTesting
