package com.coach.flame.jpa.entity

import com.coach.flame.domain.IncomeDto
import org.springframework.data.jpa.domain.AbstractPersistable
import org.springframework.lang.Nullable
import javax.persistence.*

@Table(name = "Income")
@Entity
class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Long? = null

    @Column(name = "price", nullable = false, columnDefinition = "float default '0.0'")
    var price: Float = 0.0f

    @Column(name = "status", nullable = false, columnDefinition = "varchar(100) default 'PENDING'")
    var status: String = "PENDING"

    fun toDto(): IncomeDto {
        return IncomeDto(price, IncomeDto.IncomeStatus.valueOf(status))
    }

    companion object {
        fun IncomeDto.toIncome(): Income {

            val income = Income()
            income.id = id
            income.price = price
            income.status = status.name

            return income
        }
    }

}
