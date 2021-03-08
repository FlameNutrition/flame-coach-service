package com.coach.flame.jpa.entity

import org.springframework.data.jpa.domain.AbstractPersistable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "Country_Config")
class CountryConfig(

    @Column(nullable = false)
    val countryCode: String,

    @Column(nullable = false)
    val externalValue: String

) : AbstractPersistable<Long>()