package com.coach.flame.jpa.entity

import org.springframework.data.jpa.domain.AbstractPersistable
import javax.persistence.*

@Entity
@Table(name = "GenderConfig")
class GenderConfig(

    @Column(nullable = false)
    val genderCode: String,

    @Column(nullable = false)
    val externalValue: String
    
) : AbstractPersistable<Long>()