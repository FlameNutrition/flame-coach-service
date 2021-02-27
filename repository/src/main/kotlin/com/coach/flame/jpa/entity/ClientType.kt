package com.coach.flame.jpa.entity

import org.springframework.data.jpa.domain.AbstractPersistable
import javax.persistence.*

@Entity
@Table(name = "ClientType")
class ClientType(

    @Column(nullable = false)
    val type: String,

    @OneToMany(mappedBy = "clientType", fetch = FetchType.LAZY)
    val clients: MutableList<Client> = mutableListOf()

) : AbstractPersistable<Long>()