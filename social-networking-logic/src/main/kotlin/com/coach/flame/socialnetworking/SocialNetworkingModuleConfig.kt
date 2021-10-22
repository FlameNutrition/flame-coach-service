package com.coach.flame.socialnetworking

import com.coach.flame.FlameCoachRepoConfig
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@ComponentScan("com.coach.flame.socialnetworking")
@Import(value = [FlameCoachRepoConfig::class])
class SocialNetworkingModuleConfig
