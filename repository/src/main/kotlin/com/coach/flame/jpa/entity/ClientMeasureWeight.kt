package com.coach.flame.jpa.entity

import com.coach.flame.domain.MeasureDto
import org.springframework.lang.Nullable
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "Client_Measure_Weight")
class ClientMeasureWeight(

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "Client_Measure_Weight_Generator")
    @SequenceGenerator(
        name = "Client_Measure_Weight_Generator",
        sequenceName = "Client_Measure_Weight_Seq",
        allocationSize = 1)
    @Nullable
    internal var id: Long? = null,

    @Column(nullable = false)
    val weight: Float,

    @Column(nullable = false)
    val measureDate: LocalDate,

    ) {

    fun toDto(): MeasureDto {
        return MeasureDto(
            id = this.id,
            date = this.measureDate,
            value = this.weight
        )
    }

    companion object {
        fun MeasureDto.toClientMeasureWeight(): ClientMeasureWeight {
            val clientMeasureWeigh = ClientMeasureWeight(
                weight = value,
                measureDate = date
            )

            clientMeasureWeigh.id = id
            return clientMeasureWeigh
        }
    }

}
