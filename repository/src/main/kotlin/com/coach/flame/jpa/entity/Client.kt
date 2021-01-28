package com.coach.flame.jpa.entity

import java.sql.Date
import javax.persistence.*

@Entity
//FIXME: This is not working. I want to change the table names
@Table(name = "Client")
class Client(
    val firstName: String,
    val lastName: String,
    val birthday: Date,
    @OneToOne
    val user: User,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type")
    val clientType: ClientType
) : AbstractJpaPersistable<Long>()