package com.coach.flame.jpa.entity

import org.hibernate.annotations.Type
import org.springframework.data.jpa.domain.AbstractPersistable
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "Coach")
class Coach(

    @Type(type = "uuid-char")
    @Column(nullable = false, unique = true)
    val uuid: UUID,

    @Column(nullable = false)
    var firstName: String,

    @Column(nullable = false)
    var lastName: String,

    @Column(nullable = true)
    var birthday: LocalDate? = null,

    @Column(nullable = true)
    var phoneCode: String? = null,

    @Column(nullable = true)
    var phoneNumber: String? = null,

    @ManyToOne(optional = true)
    @JoinColumn(name = "countryFk", referencedColumnName = "id")
    var country: CountryConfig? = null,

    @ManyToOne(optional = true)
    @JoinColumn(name = "genderFk", referencedColumnName = "id")
    var gender: GenderConfig? = null,

    @OneToOne(optional = false, fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "userFk", referencedColumnName = "id")
    val user: User,

    @ManyToOne(optional = false)
    @JoinColumn(name = "clientTypeFk", referencedColumnName = "id")
    val clientType: ClientType,

    @OneToMany(mappedBy = "coach")
    val clients: List<Client> = mutableListOf(),

    @Column(nullable = false, columnDefinition = "DATE")
    val registrationDate: LocalDate,
) : AbstractPersistable<Long>()
