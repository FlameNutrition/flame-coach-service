package com.coach.flame.api.dailyTask.request

data class DailyTaskFilter(
    val type: String,
    val values: List<String>,
) {

    enum class Filter(
        val numOfParams: Int,
    ) {
        IDENTIFIER(1),
        BETWEEN_DATES(2)
    }

    fun verifyNumOfParams() {
        if (Filter.valueOf(type).numOfParams != values.size) {
            throw IllegalStateException("$type has an invalid number of params")
        }
    }

}
