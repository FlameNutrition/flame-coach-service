package com.coach.flame.jpa.entity

import com.coach.flame.domain.LoginInfoDto
import com.coach.flame.jpa.entity.UserSession.Companion.toUserSession
import org.springframework.data.jpa.domain.AbstractPersistable
import javax.persistence.*

@Entity
@Table(name = "User")
class User(

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false)
    var keyDecrypt: String,

    @OneToOne(optional = false, fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "userSessionFk", referencedColumnName = "id", nullable = false)
    val userSession: UserSession,

    @OneToOne(mappedBy = "user")
    val client: Client? = null,

    @OneToOne(mappedBy = "user")
    val coach: Coach? = null,
) : AbstractPersistable<Long>() {

    fun toDto(): LoginInfoDto {
        return LoginInfoDto(
            userId = this.id,
            sessionId = this.userSession.id,
            username = this.email,
            password = this.password,
            keyDecrypt = this.keyDecrypt,
            expirationDate = this.userSession.expirationDate,
            token = this.userSession.token
        )
    }

    companion object {
        fun LoginInfoDto.toUser(): User {
            requireNotNull(keyDecrypt) { "keyDecrypt can not be null" }
            val user = User(username, password, keyDecrypt!!, this.toUserSession())
            user.id = userId
            return user
        }
    }

}
