package com.coach.flame.testing.component.base.mock

import com.coach.flame.jpa.entity.Coach
import com.coach.flame.jpa.repository.CoachRepository
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.slot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestComponent
import java.util.*

@TestComponent
class MockCoachRepository {

    @Autowired
    private lateinit var coachRepositoryMock: CoachRepository

    fun findByUuid(uuid: UUID, coach: Coach) {
        mockFindByUuid(uuid, coach)
    }

    fun findByUuidThrowsException(uuid: UUID) {
        mockFindByUuid(uuid, null)
    }

    fun saveAndFlush(): CapturingSlot<Coach> {
        val coachCaptured = slot<Coach>()

        every {
            coachRepositoryMock.saveAndFlush(capture(coachCaptured))
        } answers {
            coachCaptured.captured
        }

        return coachCaptured
    }

    private fun mockFindByUuid(uuid: UUID, coach: Coach?) {
        every { coachRepositoryMock.findByUuid(uuid) } returns (coach)
    }

}
