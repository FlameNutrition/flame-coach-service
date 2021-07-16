package com.coach.flame.jpa.repository.operations

import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.Coach
import java.util.*

interface ClientRepositoryOperation {

    fun getClient(identifier: UUID): Client

}
