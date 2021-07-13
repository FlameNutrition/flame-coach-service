package com.coach.flame.jpa.repository.operations

import com.coach.flame.domain.CoachDto
import com.coach.flame.failure.exception.CustomerNotFoundException
import com.coach.flame.jpa.entity.Coach
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Component("coachOperations")
class CoachRepositoryOperationImpl(
    @PersistenceContext private val entityManagerFactory: EntityManager,
) : CoachRepositoryOperation {

    @Transactional(readOnly = true)
    override fun getCoach(identifier: UUID): CoachDto {

        val result =
            entityManagerFactory.createQuery("select coach from Coach coach where coach.uuid = :uuid",
                Coach::class.java)
                .setParameter("uuid", identifier)
                .resultList

        if (result.isEmpty()) {
            throw CustomerNotFoundException("Could not find any coach with uuid: $identifier.")
        }

        return result.first().toDto()

    }
}
