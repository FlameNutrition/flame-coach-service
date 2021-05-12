package com.coach.flame.dailyTask.filter

import com.coach.flame.jpa.entity.DailyTask
import com.coach.flame.jpa.repository.criteria.DailyTaskCriteria
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate

class BetweenDatesFilter(
    private val from: LocalDate,
    private val to: LocalDate,
) : Filter {

    override fun getFilter(): Specification<DailyTask> {
        return DailyTaskCriteria.dailyTaskBetweenDate(from, to)
    }

}
