package com.coach.flame.jpa.entity

import javax.persistence.Entity
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "User")
class User(
    val username: String,
    val email: String,
    val password: String,
    @OneToOne(mappedBy = "user")
    val client: Client
) : AbstractJpaPersistable<Long>()