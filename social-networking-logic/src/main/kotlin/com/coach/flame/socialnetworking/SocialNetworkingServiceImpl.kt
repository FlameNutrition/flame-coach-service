package com.coach.flame.socialnetworking

import com.coach.flame.domain.LookingForCoachDto
import com.coach.flame.jpa.repository.operations.ClientRepositoryOperation
import org.springframework.stereotype.Service
import java.util.*

@Service
class SocialNetworkingServiceImpl(
    private val clientRepositoryOperation: ClientRepositoryOperation
) : SocialNetworkingService {

    override fun getLookingForCoachStatus(client: UUID): LookingForCoachDto {
        return clientRepositoryOperation
            .getClient(client)
            .clientLookingForCoach
            .toDto()
    }

    override fun enableLookingForCoach(client: UUID): LookingForCoachDto {
        TODO("Not yet implemented")
    }

    override fun disableLookingForCoach(client: UUID): LookingForCoachDto {
        TODO("Not yet implemented")
    }

}