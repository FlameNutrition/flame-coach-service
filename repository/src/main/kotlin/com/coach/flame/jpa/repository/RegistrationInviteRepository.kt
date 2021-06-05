package com.coach.flame.jpa.repository;

import com.coach.flame.jpa.entity.RegistrationInvite
import org.springframework.data.jpa.repository.JpaRepository

interface RegistrationInviteRepository : JpaRepository<RegistrationInvite, Long>{

    fun findByRegistrationKeyIs(registrationKey: String): RegistrationInvite?

    fun existsByRegistrationKeyIs(registrationKey: String): Boolean

}
