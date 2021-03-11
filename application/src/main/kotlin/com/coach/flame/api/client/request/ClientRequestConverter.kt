package com.coach.flame.api.client.request

import com.coach.flame.domain.ClientDto
import com.coach.flame.domain.ClientTypeDto
import com.coach.flame.domain.LoginInfoDto
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import java.util.*

@Component
class ClientRequestConverter : Converter<ClientRequest, ClientDto> {

    override fun convert(clientRequest: ClientRequest): ClientDto {


        val clientTypeDto = clientRequest.type?.let {
            try {
                ClientTypeDto.valueOf(clientRequest.type.toUpperCase())
            } catch (ex: Exception) {
                throw IllegalArgumentException("Invalid value in type parameter")
            }
        }

        return ClientDto(
            identifier = UUID.randomUUID(),
            firstName = clientRequest.firstname!!,
            lastName = clientRequest.lastname!!,
            clientType = clientTypeDto!!,
            loginInfo = LoginInfoDto(
                username = clientRequest.email!!,
                password = clientRequest.password!!,
            )
        )
    }

}