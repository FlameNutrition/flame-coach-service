package com.coach.flame

import com.coach.flame.config.FlameCoachRepoConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(FlameCoachRepoConfig::class)
class FlameCoachServiceApplication