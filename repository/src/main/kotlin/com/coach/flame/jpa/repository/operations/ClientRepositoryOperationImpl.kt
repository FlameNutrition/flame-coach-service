package com.coach.flame.jpa.repository.operations

import com.coach.flame.failure.exception.CustomerNotFoundException
import com.coach.flame.jpa.entity.Client
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Component("clientOperations")
class ClientRepositoryOperationImpl(
    @PersistenceContext private val entityManagerFactory: EntityManager,
) : ClientRepositoryOperation {

    @Transactional(readOnly = true)
    override fun getClient(identifier: UUID): Client {

        val result =
            entityManagerFactory.createQuery("select client from Client client where client.uuid = :uuid",
                Client::class.java)
                .setParameter("uuid", identifier)
                .resultList

        if (result.isEmpty()) {
            throw CustomerNotFoundException("Could not find any client with uuid: $identifier.")
        }

        return result.first()

    }
}
