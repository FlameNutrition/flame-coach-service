package com.coach.flame.jpa.repository.operations

import com.coach.flame.failure.exception.CustomerNotFoundException
import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.Coach
import com.coach.flame.jpa.entity.maker.ClientBuilder
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
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.TypedQuery

@ExtendWith(MockKExtension::class)
class ClientRepositoryOperationImplTest {

    @MockK
    private lateinit var entityManagerFactory: EntityManager

    @InjectMockKs
    private lateinit var clientRepositoryOperationImpl: ClientRepositoryOperationImpl

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test get client`() {

        val uuid = UUID.randomUUID()
        val query = mockk<TypedQuery<Client>>()

        every { entityManagerFactory.createQuery(any(), Client::class.java) } returns query
        every { query.setParameter(any<String>(), any()) } returns query
        every { query.resultList } returns listOf(ClientBuilder.default())

        clientRepositoryOperationImpl.getClient(uuid)

        verify {
            entityManagerFactory.createQuery("select client from Client client where client.uuid = :uuid",
                Client::class.java)
        }
        verify { query.setParameter("uuid", uuid) }

    }

    @Test
    fun `test get client throw CustomerNotFoundException`() {

        val uuid = UUID.randomUUID()
        val query = mockk<TypedQuery<Client>>()

        every { entityManagerFactory.createQuery(any(), Client::class.java) } returns query
        every { query.setParameter(any<String>(), any()) } returns query
        every { query.resultList } returns emptyList()

        val result = catchThrowable { clientRepositoryOperationImpl.getClient(uuid) }

        then(result).isInstanceOf(CustomerNotFoundException::class.java)
        then(result).hasMessageContaining("Could not find any client with uuid:")

    }

}
