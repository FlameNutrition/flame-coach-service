package com.coach.flame.jpa.entity

import org.springframework.data.jpa.domain.AbstractPersistable
import java.sql.Date
import javax.persistence.*

@Entity
@Table(name = "ClientMeasureWeight")
class ClientMeasureWeight(

    @Column(nullable = false)
    val weight: Float,

    @Column(nullable = false)
    val measureDate: Date,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "clientFk", referencedColumnName = "id")
    val client: Client

) : AbstractPersistable<Long>()