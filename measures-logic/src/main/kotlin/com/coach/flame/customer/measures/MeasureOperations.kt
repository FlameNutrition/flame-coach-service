package com.coach.flame.customer.measures

import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.MeasureDto

internal interface MeasureOperations {

    fun get(clientDto: ClientDto): ClientDto

    fun add(clientDto: ClientDto, measure: MeasureDto): ClientDto

    fun update(clientDto: ClientDto, measure: MeasureDto): ClientDto

    fun delete(clientDto: ClientDto, measure: MeasureDto): ClientDto

}
