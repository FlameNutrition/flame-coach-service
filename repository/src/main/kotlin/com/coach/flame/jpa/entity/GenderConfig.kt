package com.coach.flame.jpa.entity

import com.coach.flame.domain.GenderDto
import org.springframework.data.jpa.domain.AbstractPersistable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "Gender_Config")
class GenderConfig(

    @Column(nullable = false)
    val genderCode: String,

    @Column(nullable = false)
    val externalValue: String,

    ) : AbstractPersistable<Long>(), ConfigKey {

    override fun configKey(): String {
        return genderCode
    }

    fun toDto(): GenderDto {
        return GenderDto(
            id = this.id,
            genderCode = this.genderCode,
            externalValue = this.externalValue
        )
    }

    companion object {
        fun GenderDto.toGenderConfig(): GenderConfig {
            val genderConfig = GenderConfig(genderCode, externalValue)
            genderConfig.id = id
            return genderConfig
        }
    }
}
