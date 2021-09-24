package com.coach.flame.testing.component.base.mock

import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.ClientMeasureWeight.Companion.toClientMeasureWeight
import com.coach.flame.jpa.repository.ClientRepository
import com.coach.flame.jpa.repository.operations.ClientRepositoryOperation
import io.mockk.CapturingSlot
import io.mockk.MockKStubScope
import io.mockk.every
import io.mockk.slot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestComponent
import java.util.*
import java.util.concurrent.atomic.AtomicLong

@TestComponent
class MockClientRepository : MockRepository<MockClientRepository, Client>() {
    companion object {
        const val FIND_BY_UUID = "findByUuid"
        const val GET_CLIENT = "getClient"
        const val FIND_BY_USER_EMAIL_IS = "findByUserEmailIs"
        const val SAVE = "save"
        const val SAVE_AND_FLUSH = "saveAndFlush"
    }

    @Autowired
    private lateinit var clientRepositoryMock: ClientRepository

    @Autowired
    private lateinit var clientOperationsMock: ClientRepositoryOperation

    private fun findByUuid(uuid: UUID): MockKStubScope<Any?, Any?> {
        return every { clientRepositoryMock.findByUuid(uuid) }
    }

    private fun getClient(uuid: UUID): MockKStubScope<Any?, Any?> {
        return every { clientOperationsMock.getClient(uuid) }
    }

    private fun findByUserEmailIs(email: String): MockKStubScope<Any?, Any?> {
        return every { clientRepositoryMock.findByUserEmailIs(email) }
    }

    private fun save(): CapturingSlot<Client> {
        val clientCaptured = slot<Client>()
        every { clientRepositoryMock.save(capture(clientCaptured)) } answers { clientCaptured.captured }
        return clientCaptured
    }

    private fun saveAndFlush(): CapturingSlot<Client> {
        val clientCaptured = slot<Client>()
        val identifierMeasures = AtomicLong(1)

        every { clientRepositoryMock.saveAndFlush(capture(clientCaptured)) } answers {
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

    override fun returnsBool(f: () -> Boolean) {
        throw UnsupportedOperationException("returnsBool doest not have any method implemented!")
    }

    override fun returnsMulti(f: () -> List<Client?>) {
        throw UnsupportedOperationException("returnsMulti doest not have any method implemented!")
    }

    override fun returns(f: () -> Client?) {

        val mockKStubScope: MockKStubScope<Any?, Any?> = when (mockMethod) {
            FIND_BY_UUID ->
                findByUuid(
                    (mockParams.getOrElse("uuid") { throw RuntimeException("Missing uuid param") } as UUID)
                )
            GET_CLIENT ->
                getClient(
                    (mockParams.getOrElse("uuid") { throw RuntimeException("Missing uuid param") } as UUID)
                )
            FIND_BY_USER_EMAIL_IS ->
                findByUserEmailIs(
                    (mockParams.getOrElse("email") { throw RuntimeException("Missing email param") } as String)
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

    override fun capture(): CapturingSlot<Client> {

        return when (mockMethod) {
            SAVE -> save()
            SAVE_AND_FLUSH -> saveAndFlush()
            else -> throw RuntimeException("Missing mock method name!")
        }

    }

}
