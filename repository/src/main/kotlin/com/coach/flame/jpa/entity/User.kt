package com.coach.flame.jpa.entity

import org.springframework.data.jpa.domain.AbstractPersistable
import javax.persistence.*

@Entity
@Table(name = "User")
class User(

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @OneToOne(optional = false, fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "userSessionFk", referencedColumnName = "id", nullable = false)
    val userSession: UserSession,

    @OneToOne(mappedBy = "user")
    val client: Client? = null,
) : AbstractPersistable<Long>()