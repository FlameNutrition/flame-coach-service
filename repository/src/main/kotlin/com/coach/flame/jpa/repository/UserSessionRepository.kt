package com.coach.flame.jpa.repository

import com.coach.flame.jpa.entity.UserSession
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserSessionRepository : JpaRepository<UserSession, Long> {

    fun findByToken(token: UUID): UserSession?

}