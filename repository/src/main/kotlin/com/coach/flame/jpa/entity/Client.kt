package com.coach.flame.jpa.entity

import org.hibernate.annotations.Type
import org.springframework.data.jpa.domain.AbstractPersistable
import java.sql.Date
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "Client")
class Client(

    @Type(type = "uuid-char")
    @Column(nullable = false, unique = true)
    val uuid: UUID,

    @Column(nullable = false)
    val firstName: String,

    @Column(nullable = false)
    val lastName: String,

    @Column(nullable = false)
    val birthday: Date,

    @Column(nullable = false)
    val height: Float,

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "userFk", referencedColumnName = "id")
    val user: User,

    @ManyToOne(optional = false)
    @JoinColumn(name = "clientTypeFk", referencedColumnName = "id")
    val clientType: ClientType,

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    val clientMeasureWeight: MutableList<ClientMeasureWeight>,

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    val dailyClientTask: MutableList<DailyTask>

) : AbstractPersistable<Long>()