package com.coach.flame.jpa.repository.criteria

import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.Client_
import com.coach.flame.jpa.entity.DailyTask
import com.coach.flame.jpa.entity.DailyTask_
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate
import java.util.*
import javax.persistence.criteria.Join

class DailyTaskCriteria {

    companion object {
        fun dailyTaskClient(uuid: UUID): Specification<DailyTask> =
            Specification { root, _, criteriaBuilder ->
                val dailyTaskRel: Join<DailyTask, Client> = root.join(DailyTask_.client)
                criteriaBuilder.equal(dailyTaskRel.get(Client_.uuid), uuid)
            }

        fun dailyTaskBetweenDate(initDate: LocalDate, finalDate: LocalDate): Specification<DailyTask> =
            Specification { root, _, criteriaBuilder ->
                criteriaBuilder.between(root.get(DailyTask_.date), initDate, finalDate)
            }
    }

}