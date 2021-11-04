package com.coach.flame.jpa.entity

import com.coach.flame.domain.*
import com.coach.flame.jpa.entity.ClientMeasureWeight.Companion.toClientMeasureWeight
import com.coach.flame.jpa.entity.ClientType.Companion.toClientType
import com.coach.flame.jpa.entity.Coach.Companion.toCoach
import com.coach.flame.jpa.entity.CountryConfig.Companion.toCountryConfig
import com.coach.flame.jpa.entity.GenderConfig.Companion.toGenderConfig
import com.coach.flame.jpa.entity.User.Companion.toUser
import org.springframework.data.jpa.domain.AbstractPersistable
import javax.persistence.*

@Table(name = "Client_Looking_For_Coach")
@Entity
class ClientLookingForCoach : AbstractPersistable<Long>() {

    @Column(name = "isEnable", nullable = false, columnDefinition = "tinyint(1) default 0")
    var isEnable: Boolean = false

    @OneToOne(mappedBy = "clientLookingForCoach")
    var client: Client? = null

    @Column(name = "description", nullable = true)
    var description: String? = null

    fun toDto(): LookingForCoachDto {
        return LookingForCoachDto(
            isEnable,
            description
        )
    }
}