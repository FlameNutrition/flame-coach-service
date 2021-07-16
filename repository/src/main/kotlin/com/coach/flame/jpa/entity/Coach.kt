package com.coach.flame.jpa.entity

import com.coach.flame.domain.CoachDto
import com.coach.flame.jpa.entity.ClientType.Companion.toClientType
import com.coach.flame.jpa.entity.CountryConfig.Companion.toCountryConfig
import com.coach.flame.jpa.entity.GenderConfig.Companion.toGenderConfig
import com.coach.flame.jpa.entity.User.Companion.toUser
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
    var clients: MutableList<Client> = mutableListOf(),

    @Column(nullable = false, columnDefinition = "DATE")
    val registrationDate: LocalDate,
) : AbstractPersistable<Long>() {

    fun toDto(): CoachDto {

        val coachDto = CoachDto(
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
            registrationDate = this.registrationDate)

        val listOfClients = this.clients
            .map { it.toDto(coachDto) }
            .toSet()

        return coachDto.copy(listOfClients = listOfClients)

    }

    companion object {
        fun CoachDto.toCoach(): Coach {
            requireNotNull(loginInfo) { "loginInfo can not be null" }

            val coach = Coach(
                uuid = identifier,
                firstName = firstName,
                lastName = lastName,
                birthday = birthday,
                phoneCode = phoneCode,
                phoneNumber = phoneNumber,
                country = country?.toCountryConfig(),
                gender = gender?.toGenderConfig(),
                user = loginInfo!!.toUser(),
                clientType = customerType.toClientType(),
                registrationDate = registrationDate)

            coach.id = id
            return coach
        }
    }

}
