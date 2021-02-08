package com.coach.flame.jpa.entity

import org.springframework.data.jpa.domain.AbstractPersistable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "User")
class User(

    @Column(nullable = false)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @OneToOne(mappedBy = "user")
    val client: Client

) : AbstractPersistable<Long>()