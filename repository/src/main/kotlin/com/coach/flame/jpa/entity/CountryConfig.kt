package com.coach.flame.jpa.entity

import org.springframework.data.jpa.domain.AbstractPersistable
import javax.persistence.*

@Entity
@Table(name = "CountryConfig")
class CountryConfig(

    @Column(nullable = false)
    val countryCode: String,

    @Column(nullable = false)
    val externalValue: String

) : AbstractPersistable<Long>()