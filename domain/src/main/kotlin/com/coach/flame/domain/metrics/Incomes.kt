package com.coach.flame.domain.metrics

data class Incomes(
    val total: Int = 0,
    val pending: Int = 0,
    val accepted: Int = 0,
    val rejected: Int = 0,
) {
    override fun toString(): String {
        return "Incomes(" +
                "total=$total, " +
                "pending=$pending, " +
                "accepted=$accepted, " +
                "rejected=$rejected" +
                ")"
    }
}
