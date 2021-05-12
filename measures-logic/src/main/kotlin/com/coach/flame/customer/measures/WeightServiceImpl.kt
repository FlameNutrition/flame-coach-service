package com.coach.flame.customer.measures

import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.MeasureDto
import com.coach.flame.domain.MeasureTypeDto
import com.coach.flame.jpa.entity.Client.Companion.toClient
import com.coach.flame.jpa.repository.ClientRepository
import com.coach.flame.measures.WeightConverter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
internal class WeightServiceImpl(
    private val clientRepository: ClientRepository,
) : MeasureOperations {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(WeightServiceImpl::class.java)
    }

    override fun get(clientDto: ClientDto): ClientDto {
        LOGGER.info("opr=get, msg='Get list of weights for customer', customer={}", clientDto.identifier)

        val listOfWeights = measureConverter(clientDto.measureType, clientDto.weightMeasureTimeline)

        return clientDto.copy(weightMeasureTimeline = listOfWeights.toMutableList())
    }

    @Transactional
    override fun add(clientDto: ClientDto, measure: MeasureDto): ClientDto {
        LOGGER.info("opr=add, msg='Adding a new weight for customer', customer={}, weight={}",
            clientDto.identifier,
            measure)

        var weightToPersist = measure

        /**
         * All the measures must be saved using the measure type: [MeasureTypeDto.KG_CM]
         */
        if (MeasureTypeDto.KG_CM !== clientDto.measureType) {
            weightToPersist = when {
                MeasureTypeDto.LBS_IN === clientDto.measureType -> {
                    lbsToKg(measure)
                }
                else -> throw UnsupportedOperationException("MeasureTypeDto does not supported")
            }
        }

        clientDto.weightMeasureTimeline.add(weightToPersist)

        val client = clientRepository.saveAndFlush(clientDto.toClient())

        LOGGER.debug("opr=add, msg='Weight added with success', customer={}")

        val newClientDto = client.toDto()
        val listOfWeights = measureConverter(clientDto.measureType, newClientDto.weightMeasureTimeline)

        return newClientDto.copy(weightMeasureTimeline = listOfWeights.toMutableList())
    }

    @Transactional
    override fun update(clientDto: ClientDto, measure: MeasureDto): ClientDto {
        LOGGER.info("opr=update, msg='Update weight for customer', customer={}, weight={}",
            clientDto.identifier,
            measure)

        try {
            val measureToUpdate = clientDto.weightMeasureTimeline.first { it.id == measure.id }

            var weightToPersist = measure

            /**
             * All the measures must be saved using the measure type: [MeasureTypeDto.KG_CM]
             */
            if (MeasureTypeDto.KG_CM !== clientDto.measureType) {
                weightToPersist = when {
                    MeasureTypeDto.LBS_IN === clientDto.measureType -> {
                        lbsToKg(measure)
                    }
                    else -> throw UnsupportedOperationException("MeasureTypeDto does not supported")
                }
            }

            clientDto.weightMeasureTimeline.remove(measureToUpdate)
            clientDto.weightMeasureTimeline.add(measureToUpdate.copy(value = weightToPersist.value,
                date = weightToPersist.date))

            val client = clientRepository.saveAndFlush(clientDto.toClient())

            LOGGER.debug("opr=update, msg='Weight updated with success', customer={}")

            val newClientDto = client.toDto()
            val listOfWeights = measureConverter(clientDto.measureType, newClientDto.weightMeasureTimeline)

            return newClientDto.copy(weightMeasureTimeline = listOfWeights.toMutableList())
        } catch (ex: NoSuchElementException) {
            throw MeasureNotFoundException("Measure is not present in the list")
        }
    }

    @Transactional
    override fun delete(clientDto: ClientDto, measure: MeasureDto): ClientDto {

        LOGGER.info("opr=delete, msg='Delete weight from customer', customer={}, weight={}",
            clientDto.identifier,
            measure)

        try {
            if (clientDto.weightMeasureTimeline.remove(measure)) {
                val client = clientRepository.saveAndFlush(clientDto.toClient())

                val newClientDto = client.toDto()
                val listOfWeights = measureConverter(clientDto.measureType, newClientDto.weightMeasureTimeline)
                return clientDto.copy(weightMeasureTimeline = listOfWeights.toMutableList())
            } else {
                throw MeasureNotFoundException("Measure is not present in the list")
            }
        } catch (ex: IllegalArgumentException) {
            throw MeasureNotFoundException("Did not found any measure with the provided identifier")
        }
    }

    private fun measureConverter(type: MeasureTypeDto, listOfWeights: List<MeasureDto>): List<MeasureDto> {
        return when (type) {
            MeasureTypeDto.KG_CM -> {
                listOfWeights
            }
            MeasureTypeDto.LBS_IN -> {
                LOGGER.debug("opr=get, msg='Convert all measures using client config'")
                listOfWeights.map(kgToLbs)
            }
            else -> throw UnsupportedOperationException("MeasureTypeDto does not supported")
        }
    }

    private val lbsToKg: (MeasureDto) -> MeasureDto = {
        val newValue = WeightConverter.convertWeightToKgs(it.value, WeightConverter.WeightType.LBS)

        LOGGER.info("opr=lbsToKg, msg='Converting weight from lbs to kgs', oldWeight={}, newWeight={}",
            it.value,
            newValue)
        it.copy(value = newValue)
    }

    private val kgToLbs: (MeasureDto) -> MeasureDto = {
        val newValue = WeightConverter.convertWeightToLbs(it.value, WeightConverter.WeightType.KGS)

        LOGGER.info("opr=kgToLbs, msg='Converting weight from kgs to lbs', oldWeight={}, newWeight={}",
            it.value,
            newValue)
        it.copy(value = newValue)
    }

}
