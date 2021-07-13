package com.coach.flame.metrics

import com.coach.flame.domain.ClientStatusDto
import com.coach.flame.domain.maker.ClientDtoBuilder
import com.coach.flame.domain.maker.ClientDtoMaker
import com.coach.flame.domain.maker.CoachDtoBuilder
import com.coach.flame.domain.maker.CoachDtoMaker
import com.coach.flame.jpa.repository.operations.CoachRepositoryOperation
import com.natpryce.makeiteasy.MakeItEasy.with
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class ClientsMetricsServiceImplTest {

    @MockK(relaxed = true)
    private lateinit var coachRepositoryOperation: CoachRepositoryOperation

    @InjectMockKs
    private lateinit var classToTest: ClientsMetricsServiceImpl

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test get clients metrics`() {

        val uuid = UUID.randomUUID()

        every {
            coachRepositoryOperation.getCoach(uuid)
        } returns CoachDtoBuilder.maker()
            .but(with(CoachDtoMaker.listOfClients, setOf(
                ClientDtoBuilder.makerWithLoginInfo()
                    .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.PENDING))
                    .make(),
                ClientDtoBuilder.makerWithLoginInfo()
                    .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.PENDING))
                    .make(),
                ClientDtoBuilder.makerWithLoginInfo()
                    .but(with(ClientDtoMaker.clientStatus, ClientStatusDto.ACCEPTED))
                    .make()
            )))
            .make()

        val result = classToTest.getClientsMetrics(uuid)

        then(result.numberOfClientsAccepted).isEqualTo(1)
        then(result.numberOfClientsPending).isEqualTo(2)
        then(result.numberOfTotalClients).isEqualTo(3)

    }

}
