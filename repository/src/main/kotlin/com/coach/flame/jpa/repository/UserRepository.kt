package com.coach.flame.jpa.repository

import com.coach.flame.jpa.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun findUserByEmailAndPassword(email: String, password: String): User?

}