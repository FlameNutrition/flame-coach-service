package com.coach.flame.customer.measures

import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.MeasureDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MeasureFactory {

    @Autowired
    private lateinit var weightServiceImpl: MeasureOperations

    enum class Measure {
        WEIGHT
    }

    enum class Operation {
        GET, ADD, UPDATE, DELETE
    }

    fun executeOperation(
        measureType: Measure,
        operation: Operation,
        clientDto: ClientDto,
        measureDto: MeasureDto? = null,
    ): ClientDto {

        if (measureType === Measure.WEIGHT) {

            if (operation === Operation.GET) {
                return weightServiceImpl.get(clientDto)
            }

            if (operation === Operation.ADD) {
                return weightServiceImpl.add(clientDto, measureDto!!)
            }

            if (operation === Operation.UPDATE) {
                return weightServiceImpl.update(clientDto, measureDto!!)
            }

            if (operation === Operation.DELETE) {
                return weightServiceImpl.delete(clientDto, measureDto!!)
            }
        }

        throw UnsupportedOperationException("Please choose another operation")

    }

}
