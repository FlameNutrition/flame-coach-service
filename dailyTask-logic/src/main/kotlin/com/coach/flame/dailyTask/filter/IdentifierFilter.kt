package com.coach.flame.dailyTask.filter

import com.coach.flame.jpa.entity.DailyTask
import com.coach.flame.jpa.repository.criteria.DailyTaskCriteria
import org.springframework.data.jpa.domain.Specification
import java.util.*

class IdentifierFilter(
    private val identifier: UUID,
) : Filter {

    override fun getFilter(): Specification<DailyTask> {
        return DailyTaskCriteria.dailyTaskClient(identifier)
    }
    
}