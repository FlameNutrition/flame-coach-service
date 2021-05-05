package com.coach.flame.jpa.entity

import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.ClientStatusDto
import com.coach.flame.domain.CoachDto
import com.coach.flame.domain.MeasureTypeDto
import com.coach.flame.jpa.converter.MeasureConfigConverter
import com.coach.flame.jpa.entity.ClientMeasureWeight.Companion.toClientMeasureWeight
import com.coach.flame.jpa.entity.ClientType.Companion.toClientType
import com.coach.flame.jpa.entity.Coach.Companion.toCoach
import com.coach.flame.jpa.entity.CountryConfig.Companion.toCountryConfig
import com.coach.flame.jpa.entity.GenderConfig.Companion.toGenderConfig
import com.coach.flame.jpa.entity.User.Companion.toUser
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

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH])
    @JoinColumn(name = "clientFk", referencedColumnName = "id")
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
) : AbstractPersistable<Long>() {

    fun toDto(coachDto: CoachDto?): ClientDto {

        val listOfWeightsTimeline = this.clientMeasureWeight
            .map { it.toDto() }

        return ClientDto(
            id = this.id,
            identifier = this.uuid,
            firstName = this.firstName,
            lastName = this.lastName,
            birthday = this.birthday,
            phoneCode = this.phoneCode,
            phoneNumber = this.phoneNumber,
            country = this.country?.toDto(),
            gender = this.gender?.toDto(),
            customerType = this.clientType.toDto(),
            loginInfo = this.user.toDto(),
            registrationDate = this.registrationDate,
            weight = this.weight,
            height = this.height,
            measureType = MeasureTypeDto.valueOf(this.measureConfig.name),
            clientStatus = ClientStatusDto.valueOf(this.clientStatus.name),
            coach = coachDto,
            weightMeasureTimeline = listOfWeightsTimeline.toMutableList()
        )

    }

    fun toDto(): ClientDto {
        return toDto(null)
    }

    companion object {
        fun ClientDto.toClient(): Client {
            requireNotNull(loginInfo) { "loginInfo can not be null" }
            requireNotNull(clientStatus) { "clientStatus can not be null" }

            val listOfWeightsTimeline = weightMeasureTimeline
                .map { it.toClientMeasureWeight() }

            val client = Client(
                uuid = identifier,
                firstName = firstName,
                lastName = lastName,
                birthday = birthday,
                phoneCode = phoneCode,
                phoneNumber = phoneNumber,
                country = country?.toCountryConfig(),
                gender = gender?.toGenderConfig(),
                weight = weight,
                height = height,
                clientMeasureWeight = listOfWeightsTimeline.toMutableList(),
                measureConfig = MeasureConfig.valueOf(measureType.name),
                user = loginInfo!!.toUser(),
                clientType = customerType.toClientType(),
                clientStatus = ClientStatus.valueOf(clientStatus!!.name),
                registrationDate = registrationDate,
                coach = coach?.toCoach()
            )

            client.id = id
            return client
        }
    }

}
