package com.coach.flame.domain

class IncomeDto(
    var price: Float = 0.0f,
    var status: IncomeStatus = IncomeStatus.PENDING,
) {

    enum class IncomeStatus {
        PENDING, ACCEPTED, REJECTED
    }

    var id: Long? = null

    override fun toString(): String {
        return "IncomeDto(" +
                "price=$price, " +
                "status=$status" +
                ")"
    }

}
