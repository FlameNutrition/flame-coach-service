package com.coach.flame.api.client.response

import com.coach.flame.domain.ClientDto
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class ClientResponseConverter : Converter<ClientDto, ClientResponse> {

    override fun convert(clientDto: ClientDto): ClientResponse {

        requireNotNull(clientDto.firstName) { "firstName should not be null" }
        requireNotNull(clientDto.lastName) { "lastName should not be null" }
        requireNotNull(clientDto.loginInfo) { "loginInfo should not be null" }
        requireNotNull(clientDto.loginInfo?.token) { "loginInfo->token should not be null" }
        requireNotNull(clientDto.loginInfo?.username) { "loginInfo->username should not be null" }
        requireNotNull(clientDto.loginInfo?.expirationDate) { "loginInfo->expirationDate should not be null" }

        return ClientResponse(
            username = clientDto.loginInfo?.username!!,
            firstname = clientDto.firstName!!,
            lastname = clientDto.lastName!!,
            token = clientDto.loginInfo?.token!!,
            expiration = clientDto.loginInfo?.expirationDate!!
        )

    }

}