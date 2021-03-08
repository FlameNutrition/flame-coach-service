package com.coach.flame.jpa.entity

import org.hibernate.annotations.Type
import org.springframework.data.jpa.domain.AbstractPersistable
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "Daily_Task")
class DailyTask(

    @Type(type = "uuid-char")
    @Column(nullable = false, unique = true)
    val uuid: UUID,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val description: String,

    @Column(nullable = false)
    val date: LocalDate,

    @Column(nullable = false)
    val ticked: Boolean,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "createdByFk", referencedColumnName = "id")
    val createdBy: Client,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "clientFk", referencedColumnName = "id")
    val client: Client,
) : AbstractPersistable<Long>()