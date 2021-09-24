package com.coach.flame.testing.component.base.mock

import com.coach.flame.jpa.entity.Coach
import com.coach.flame.jpa.repository.CoachRepository
import com.coach.flame.jpa.repository.operations.CoachRepositoryOperation
import io.mockk.CapturingSlot
import io.mockk.MockKStubScope
import io.mockk.every
import io.mockk.slot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestComponent
import java.util.*

@TestComponent
class MockCoachRepository : MockRepository<MockCoachRepository, Coach>() {

    companion object {
        const val FIND_BY_UUID = "findByUuid"
        const val GET_COACH = "getCoach"
        const val SAVE_AND_FLUSH = "saveAndFlush"
    }

    @Autowired
    private lateinit var coachRepositoryMock: CoachRepository

    @Autowired
    private lateinit var coachOperationsMock: CoachRepositoryOperation

    private fun saveAndFlush(): CapturingSlot<Coach> {
        val coachCaptured = slot<Coach>()

        every {
            coachRepositoryMock.saveAndFlush(capture(coachCaptured))
        } answers {
            coachCaptured.captured
        }

        return coachCaptured
    }

    private fun findByUuid(uuid: UUID): MockKStubScope<Any?, Any?> {
        return every { coachRepositoryMock.findByUuid(uuid) }
    }

    private fun getCoach(uuid: UUID): MockKStubScope<Any?, Any?> {
        return every { coachOperationsMock.getCoach(uuid) }
    }

    override fun returnsBool(f: () -> Boolean) {
        throw UnsupportedOperationException("returnsBool doest not have any method implemented!")
    }

    override fun returnsMulti(f: () -> List<Coach?>) {
        throw UnsupportedOperationException("returnsMulti doest not have any method implemented!")
    }

    override fun returns(f: () -> Coach?) {

        val mockKStubScope: MockKStubScope<Any?, Any?> = when (mockMethod) {
            FIND_BY_UUID ->
                findByUuid(
                    (mockParams.getOrElse("uuid") { throw RuntimeException("Missing uuid param") } as UUID)
                )
            GET_COACH ->
                getCoach(
                    (mockParams.getOrElse("uuid") { throw RuntimeException("Missing uuid param") } as UUID)
                )
            else -> throw RuntimeException("Missing mock method name!")
        }

        try {
            mockKStubScope returns f.invoke()
        } catch (ex: Exception) {
            mockKStubScope throws ex
        }

        clean()

    }

    override fun capture(): CapturingSlot<Coach> {

        return when (mockMethod) {
            SAVE_AND_FLUSH -> saveAndFlush()
            else -> throw RuntimeException("Missing mock method name!")
        }

    }

}
