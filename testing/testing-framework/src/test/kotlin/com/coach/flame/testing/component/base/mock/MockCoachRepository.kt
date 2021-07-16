package com.coach.flame.testing.component.base.mock

import com.coach.flame.failure.exception.CustomerNotFoundException
import com.coach.flame.jpa.entity.Coach
import com.coach.flame.jpa.repository.CoachRepository
import com.coach.flame.jpa.repository.operations.CoachRepositoryOperation
import io.mockk.Answer
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

    @Autowired
    private lateinit var coachOperationsMock: CoachRepositoryOperation

    fun saveAndFlush(): CapturingSlot<Coach> {
        val coachCaptured = slot<Coach>()

        every {
            coachRepositoryMock.saveAndFlush(capture(coachCaptured))
        } answers {
            coachCaptured.captured
        }

        return coachCaptured
    }

    fun mockFindByUuidThrowsException(uuid: UUID) {
        every { coachOperationsMock.getCoach(uuid) } throws CustomerNotFoundException("Could not find any coach with uuid: $uuid.")
        every { coachRepositoryMock.findByUuid(uuid) } throws CustomerNotFoundException("Could not find any coach with uuid: $uuid.")
    }

    fun mockFindByUuid(uuid: UUID, coach: Coach) {
        every { coachOperationsMock.getCoach(uuid) } returns coach
        every { coachRepositoryMock.findByUuid(uuid) } returns coach
    }

}
