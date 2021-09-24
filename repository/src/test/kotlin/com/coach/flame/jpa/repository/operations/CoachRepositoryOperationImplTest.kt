package com.coach.flame.jpa.repository.operations

import com.coach.flame.failure.exception.CustomerNotFoundException
import com.coach.flame.jpa.entity.Coach
import com.coach.flame.jpa.entity.Income
import com.coach.flame.jpa.entity.maker.CoachBuilder
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.TypedQuery

@ExtendWith(MockKExtension::class)
class CoachRepositoryOperationImplTest {

    @MockK
    private lateinit var entityManagerFactory: EntityManager

    @InjectMockKs
    private lateinit var coachRepositoryOperationImpl: CoachRepositoryOperationImpl

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test get coach`() {

        val uuid = UUID.randomUUID()
        val query = mockk<TypedQuery<Coach>>()

        every { entityManagerFactory.createQuery(any(), Coach::class.java) } returns query
        every { query.setParameter(any<String>(), any()) } returns query
        every { query.resultList } returns listOf(CoachBuilder.default())

        coachRepositoryOperationImpl.getCoach(uuid)

        verify {
            entityManagerFactory.createQuery("select coach from Coach coach where coach.uuid = :uuid",
                Coach::class.java)
        }
        verify { query.setParameter("uuid", uuid) }

    }

    @Test
    fun `test get incomes`() {

        val from = LocalDate.of(2021, 1, 1)
        val to = LocalDate.of(2021, 1, 31)
        val uuid = UUID.randomUUID()
        val query = mockk<TypedQuery<Income>>()

        every { entityManagerFactory.createQuery(any(), Income::class.java) } returns query
        every { query.setParameter(any<String>(), any()) } returns query
        every { query.resultList } returns listOf()

        coachRepositoryOperationImpl.getIncome(uuid, from, to)

        val expectedQuery = "select i from Coach c " +
                "inner join Appointment a on c = a.coach " +
                "inner join Income i on i = a.income " +
                "where a.dttmStarts between :dttmFrom and :dttmTo " +
                "and c.uuid = :uuid"

        verify { entityManagerFactory.createQuery(expectedQuery, Income::class.java) }
        verify { query.setParameter("uuid", uuid) }
        verify { query.setParameter("dttmFrom", from.atStartOfDay()) }
        verify { query.setParameter("dttmTo", to.plusDays(1).atStartOfDay()) }

    }

    @Test
    fun `test get coach throw CustomerNotFoundException`() {

        val uuid = UUID.randomUUID()
        val query = mockk<TypedQuery<Coach>>()

        every { entityManagerFactory.createQuery(any(), Coach::class.java) } returns query
        every { query.setParameter(any<String>(), any()) } returns query
        every { query.resultList } returns emptyList()

        val result = catchThrowable { coachRepositoryOperationImpl.getCoach(uuid) }

        then(result).isInstanceOf(CustomerNotFoundException::class.java)
        then(result).hasMessageContaining("Could not find any coach with uuid:")

    }

}
