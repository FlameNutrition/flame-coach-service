package com.coach.flame.testing.component.base.mock

import com.coach.flame.jpa.entity.RegistrationInvite
import com.coach.flame.jpa.repository.RegistrationInviteRepository
import io.mockk.CapturingSlot
import io.mockk.MockKStubScope
import io.mockk.every
import io.mockk.slot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestComponent

@TestComponent
class MockRegistrationInviteRepository : MockRepository<MockRegistrationInviteRepository, RegistrationInvite>() {

    companion object {
        const val FIND_BY_REGISTRATION_KEY_IS = "findByRegistrationKeyIs"
        const val EXISTS_BY_REGISTRATION_KEY_IS = "existsByRegistrationKeyIs"
        const val SAVE = "save"
    }

    @Autowired
    private lateinit var registrationInviteRepository: RegistrationInviteRepository

    private fun save(): CapturingSlot<RegistrationInvite> {
        val captured = slot<RegistrationInvite>()
        every { registrationInviteRepository.save(capture(captured)) } answers { captured.captured }
        return captured
    }

    private fun findByRegistrationKeyIs(key: String): MockKStubScope<Any?, Any?> {
        return every { registrationInviteRepository.findByRegistrationKeyIs(key) }
    }

    private fun existsByRegistrationKeyIs(key: String): MockKStubScope<Boolean?, Boolean?> {
        return every { registrationInviteRepository.existsByRegistrationKeyIs(key) }
    }

    override fun returnsBool(f: () -> Boolean) {
        val mockKStubScope: MockKStubScope<Boolean?, Boolean?> = when (mockMethod) {
            EXISTS_BY_REGISTRATION_KEY_IS ->
                existsByRegistrationKeyIs(
                    (mockParams.getOrElse("key") { throw RuntimeException("Missing key param") } as String)
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

    override fun returnsMulti(f: () -> List<RegistrationInvite?>) {
        throw UnsupportedOperationException("returnsMulti doest not have any method implemented!")
    }

    override fun returns(f: () -> RegistrationInvite?) {
        val mockKStubScope: MockKStubScope<Any?, Any?> = when (mockMethod) {
            FIND_BY_REGISTRATION_KEY_IS ->
                findByRegistrationKeyIs(
                    (mockParams.getOrElse("key") { throw RuntimeException("Missing key param") } as String)
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

    override fun capture(): CapturingSlot<RegistrationInvite> {
        return when (mockMethod) {
            SAVE -> save()
            else -> throw RuntimeException("Missing mock method name!")
        }
    }

}
