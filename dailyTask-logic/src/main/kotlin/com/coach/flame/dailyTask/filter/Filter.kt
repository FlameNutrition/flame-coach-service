package com.coach.flame.dailyTask.filter

import com.coach.flame.jpa.entity.DailyTask
import org.springframework.data.jpa.domain.Specification

interface Filter {

    fun getFilter(): Specification<DailyTask>

}