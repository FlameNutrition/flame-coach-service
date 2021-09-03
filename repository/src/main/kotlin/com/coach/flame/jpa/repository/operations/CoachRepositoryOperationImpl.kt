package com.coach.flame.jpa.repository.operations

import com.coach.flame.failure.exception.CustomerNotFoundException
import com.coach.flame.jpa.entity.Coach
import com.coach.flame.jpa.entity.Income
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Component("coachOperations")
class CoachRepositoryOperationImpl(
    @PersistenceContext private val entityManagerFactory: EntityManager,
) : CoachRepositoryOperation {

    @Transactional(readOnly = true)
    override fun getCoach(identifier: UUID): Coach {

        val result = entityManagerFactory
            .createQuery("select coach from Coach coach where coach.uuid = :uuid", Coach::class.java)
            .setParameter("uuid", identifier)
            .resultList

        if (result.isEmpty()) {
            throw CustomerNotFoundException("Could not find any coach with uuid: $identifier.")
        }

        return result.first()

    }

    @Transactional(readOnly = true)
    override fun getIncome(identifier: UUID, from: LocalDate, to: LocalDate): List<Income> {

        return entityManagerFactory
            .createQuery("select i from Coach c " +
                    "inner join Appointment a on c = a.coach " +
                    "inner join Income i on i = a.income " +
                    "where a.dttmStarts between :dttmFrom and :dttmTo " +
                    "and c.uuid = :uuid", Income::class.java)
            .setParameter("uuid", identifier)
            .setParameter("dttmFrom", from)
            .setParameter("dttmTo", to)
            .resultList

    }
}
