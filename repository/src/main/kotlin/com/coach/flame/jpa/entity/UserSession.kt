package com.coach.flame.jpa.entity

import org.hibernate.annotations.Type
import org.springframework.data.jpa.domain.AbstractPersistable
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "UserSession")
class UserSession(

    @Type(type = "uuid-char")
    @Column(nullable = false, unique = true)
    val token: UUID,

    @Column(nullable = false)
    val expirationDate: LocalDateTime,

) : AbstractPersistable<Long>()