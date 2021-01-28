package com.coach.flame.jpa.entity

import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "ClientType")
class ClientType(
    val type: String
) : AbstractJpaPersistable<Long>()