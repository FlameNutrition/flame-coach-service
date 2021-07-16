package com.coach.flame.testing.component.base.mock

import com.coach.flame.failure.exception.CustomerNotFoundException
import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.ClientMeasureWeight.Companion.toClientMeasureWeight
import com.coach.flame.jpa.entity.Coach
import com.coach.flame.jpa.repository.ClientRepository
import com.coach.flame.jpa.repository.operations.ClientRepositoryOperation
import com.coach.flame.jpa.repository.operations.CoachRepositoryOperation
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.slot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestComponent
import java.util.*
import java.util.concurrent.atomic.AtomicLong

@TestComponent
class MockClientRepository {

    @Autowired
    private lateinit var clientRepositoryMock: ClientRepository

    @Autowired
    private lateinit var clientOperationsMock: ClientRepositoryOperation

    fun mockFindByUuid(uuid: UUID, client: Client) {
        every { clientOperationsMock.getClient(uuid) } returns client
        every { clientRepositoryMock.findByUuid(uuid) } returns client
    }

    fun mockFindByUuidThrowsException(uuid: UUID) {
        every { clientOperationsMock.getClient(uuid) } throws CustomerNotFoundException("Could not find any client with uuid: $uuid.")
        every { clientRepositoryMock.findByUuid(uuid) } throws CustomerNotFoundException("Could not find any client with uuid: $uuid.")
    }

    fun findByUuid(uuid: UUID, answer: Client) {
        every {
            clientRepositoryMock.findByUuid(uuid)
        } returns (answer)
    }

    fun findByUserEmailIs(email: String, answer: Client?) {
        every {
            clientRepositoryMock.findByUserEmailIs(email)
        } returns (answer)
    }

    fun findByUuidThrowsException(uuid: UUID) {
        every {
            clientRepositoryMock.findByUuid(uuid)
        } returns (null)
    }

    fun save(): CapturingSlot<Client> {
        val clientCaptured = slot<Client>()

        every {
            clientRepositoryMock.save(capture(clientCaptured))
        } answers {
            clientCaptured.captured
        }

        return clientCaptured
    }

    fun saveAndFlush(): CapturingSlot<Client> {
        val clientCaptured = slot<Client>()
        val identifierMeasures = AtomicLong(1)

        every {
            clientRepositoryMock.saveAndFlush(capture(clientCaptured))
        } answers {
            clientCaptured.captured.clientMeasureWeight.replaceAll {
                val dto = it.toDto()
                if (dto.id == null) {
                    dto.copy(id = identifierMeasures.getAndIncrement()).toClientMeasureWeight()
                } else {
                    it
                }
            }
            clientCaptured.captured
        }

        return clientCaptured
    }
}
