package com.coach.flame.jpa.entity

import com.coach.flame.domain.MeasureWeightDto
import org.springframework.data.jpa.domain.AbstractPersistable
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "Client_Measure_Weight")
class ClientMeasureWeight(

    @Column(nullable = false)
    val weight: Float,

    @Column(nullable = false)
    val measureDate: LocalDate,

    ) : AbstractPersistable<Long>() {

    fun toDto(): MeasureWeightDto {
        return MeasureWeightDto(
            id = this.id,
            date = this.measureDate,
            value = this.weight
        )
    }

    companion object {
        fun MeasureWeightDto.toClientMeasureWeight(): ClientMeasureWeight {
            val clientMeasureWeigh = ClientMeasureWeight(
                weight = value,
                measureDate = date
            )

            clientMeasureWeigh.id = id
            return clientMeasureWeigh
        }
    }

}
