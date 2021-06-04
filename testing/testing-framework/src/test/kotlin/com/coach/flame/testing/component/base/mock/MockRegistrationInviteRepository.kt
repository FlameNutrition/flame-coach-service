package com.coach.flame.testing.component.base.mock

import com.coach.flame.jpa.entity.RegistrationInvite
import com.coach.flame.jpa.repository.RegistrationInviteRepository
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.slot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestComponent

@TestComponent
class MockRegistrationInviteRepository {

    @Autowired
    private lateinit var registrationInviteRepository: RegistrationInviteRepository

    fun save(): CapturingSlot<RegistrationInvite> {
        val captured = slot<RegistrationInvite>()

        every {
            registrationInviteRepository.save(capture(captured))
        } answers {
            captured.captured
        }

        return captured
    }

}
