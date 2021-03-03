package com.coach.flame.client

import com.coach.flame.config.FlameCoachRepoConfig
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@ComponentScan(value = ["com.coach.flame.client", "com.coach.flame.domain.converters"])
@Import(value = [FlameCoachRepoConfig::class])
class ClientModuleConfig