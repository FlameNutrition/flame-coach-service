package com.coach.flame.jpa.entity

import org.springframework.data.jpa.domain.AbstractAuditable
import java.sql.Date
import javax.persistence.*

@Entity
@Table(name = "User")
class User(

    @Column(nullable = false)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @OneToOne(mappedBy = "user")
    val client: Client

) : AbstractAuditable<SysAdmin, Long>()