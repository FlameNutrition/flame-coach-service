package com.coach.flame.jpa.entity

import com.coach.flame.domain.LoginInfoDto
import org.hibernate.annotations.Type
import org.springframework.data.jpa.domain.AbstractPersistable
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "User_Session")
class UserSession(

    @Type(type = "uuid-char")
    @Column(nullable = false, unique = true)
    val token: UUID,

    @Column(nullable = false)
    var expirationDate: LocalDateTime,

    @OneToOne(mappedBy = "userSession")
    val user: User? = null,
) : AbstractPersistable<Long>() {

    companion object {
        fun LoginInfoDto.toUserSession(): UserSession {
            requireNotNull(token) { "token can not be null" }
            requireNotNull(expirationDate) { "expirationDate can not be null" }
            val userSession = UserSession(token!!, expirationDate!!)
            userSession.id = sessionId
            return userSession
        }
    }

}
