package com.coach.flame.jpa.entity

import com.coach.flame.jpa.converter.MeasureConfigConverter
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

    @Column(name = "weight", columnDefinition = "float default '0.0'")
    var weight: Float = 0.0f,

    @Column(name = "height", columnDefinition = "float default '0.0'")
    var height: Float = 0.0f,

    @Column(nullable = false, columnDefinition = "varchar(100) default 'KG_CM'")
    @Convert(converter = MeasureConfigConverter::class)
    var measureConfig: MeasureConfig = MeasureConfig.KG_CM,

    @OneToOne(optional = false, fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "userFk", referencedColumnName = "id")
    val user: User,

    @ManyToOne(optional = false)
    @JoinColumn(name = "clientTypeFk", referencedColumnName = "id")
    val clientType: ClientType,

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    val clientMeasureWeight: MutableList<ClientMeasureWeight> = mutableListOf(),

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    val dailyClientTask: MutableList<DailyTask> = mutableListOf(),

    @ManyToOne(optional = true)
    @JoinColumn(name = "coachFk", referencedColumnName = "id")
    var coach: Coach? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var clientStatus: ClientStatus,

    @Column(nullable = false, columnDefinition = "DATE")
    val registrationDate: LocalDate,
) : AbstractPersistable<Long>()
