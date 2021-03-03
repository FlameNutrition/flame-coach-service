package com.coach.flame.jpa.entity

import org.springframework.data.jpa.domain.AbstractPersistable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "GenderConfig")
class GenderConfig(

    @Column(nullable = false)
    val genderCode: String,

    @Column(nullable = false)
    val externalValue: String
    
) : AbstractPersistable<Long>()