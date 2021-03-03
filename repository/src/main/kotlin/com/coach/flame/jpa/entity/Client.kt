package com.coach.flame.jpa.entity

import org.hibernate.annotations.Type
import org.springframework.data.jpa.domain.AbstractPersistable
import java.time.LocalDate
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

    @Column(nullable = true)
    val birthday: LocalDate? = null,

    @Column(nullable = true)
    val phoneCode: String? = null,

    @Column(nullable = true)
    val phoneNumber: String? = null,

    @ManyToOne(optional = true)
    @JoinColumn(name = "countryFk", referencedColumnName = "id")
    val country: CountryConfig? = null,

    @ManyToOne(optional = true)
    @JoinColumn(name = "genderFk", referencedColumnName = "id")
    val gender: GenderConfig? = null,

    @OneToOne(optional = false, fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "userFk", referencedColumnName = "id")
    val user: User,

    @ManyToOne(optional = false)
    @JoinColumn(name = "clientTypeFk", referencedColumnName = "id")
    val clientType: ClientType,

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    val clientMeasureWeight: MutableList<ClientMeasureWeight> = mutableListOf(),

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    val dailyClientTask: MutableList<DailyTask> = mutableListOf()

) : AbstractPersistable<Long>()