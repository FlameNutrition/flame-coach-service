package com.coach.flame.jpa.repository.criteria

import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.Client_
import com.coach.flame.jpa.entity.DailyTask
import com.coach.flame.jpa.entity.DailyTask_
import com.coach.flame.jpa.repository.criteria.DailyTaskCriteria
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*
import javax.persistence.criteria.*

class DailyTaskCriteriaTest {


    @Test
    fun `test criteria dailyTaskClient`() {

        val uuid = UUID.randomUUID()

        val root = mockk<Root<DailyTask>>()
        val query = mockk<CriteriaQuery<*>>()
        val criteriaBuilder = mockk<CriteriaBuilder>()
        val joinMock = mockk<Join<DailyTask, Client>>()
        val finalPath = mockk<Path<UUID>>()

        every { root.join(DailyTask_.client) } returns joinMock
        every { joinMock.get(Client_.uuid) } returns finalPath
        every { criteriaBuilder.equal(finalPath, uuid) } returns mockk()

        val result = DailyTaskCriteria.dailyTaskClient(uuid)
            .toPredicate(root, query, criteriaBuilder)

        then(result).isNotNull

    }

    @Test
    fun `test criteria dailyTaskBetweenDate`() {

        val date0 = LocalDate.now()
        val date1 = LocalDate.now()

        val root = mockk<Root<DailyTask>>()
        val query = mockk<CriteriaQuery<*>>()
        val criteriaBuilder = mockk<CriteriaBuilder>()
        val finalPath = mockk<Path<LocalDate>>()

        every { root.get(DailyTask_.date) } returns finalPath
        every { criteriaBuilder.between(finalPath, date0, date1) } returns mockk()

        val result = DailyTaskCriteria.dailyTaskBetweenDate(date0, date1)
            .toPredicate(root, query, criteriaBuilder)

        then(result).isNotNull

    }

}
