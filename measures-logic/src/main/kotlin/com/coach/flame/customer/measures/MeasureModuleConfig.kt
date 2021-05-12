package com.coach.flame.customer.measures

import com.coach.flame.FlameCoachRepoConfig
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@ComponentScan("com.coach.flame.customer.measures")
@Import(value = [FlameCoachRepoConfig::class])
class MeasureModuleConfig
