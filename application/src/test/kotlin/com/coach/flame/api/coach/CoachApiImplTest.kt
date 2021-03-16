package com.coach.flame.api.coach

import com.coach.flame.customer.coach.CoachService
import com.coach.flame.domain.*
import com.coach.flame.exception.RestInvalidRequestException
import com.natpryce.makeiteasy.MakeItEasy.with
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class CoachApiImplTest {

    @MockK
    private lateinit var coachService: CoachService

    @InjectMockKs
    private lateinit var classToTest: CoachApiImpl

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test get clients from a coach`() {

        val uuid = UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")

        val client0 = ClientDtoBuilder.maker().but(with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.default())).make()
        val client1 = ClientDtoBuilder.maker().but(with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.default())).make()
        val clients = setOf(client0, client1)
        val clientCoach = CoachDtoBuilder.maker()
            .but(with(CoachDtoMaker.loginInfo, LoginInfoDtoBuilder.default()),
                with(CoachDtoMaker.customerType, CustomerTypeDto.COACH),
                with(CoachDtoMaker.identifier, uuid),
                with(CoachDtoMaker.listOfClients, clients))
            .make()

        every { coachService.getCoachWithClientsAccepted(uuid) } returns clientCoach

        val response = classToTest.getClientsCoach(uuid.toString())

        then(response.identifier).isEqualTo(uuid)
        then(response.clientsCoach).isNotEmpty
        then(response.clientsCoach).hasSize(2)

    }

    @Test
    fun `test get clients from a coach with unexpected error`() {

        // given
        val uuid = UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")
        every { coachService.getCoachWithClientsAccepted(uuid) } throws RuntimeException("Something wrong happened")

        // when
        val thrown = catchThrowable { classToTest.getClientsCoach(uuid.toString()) }

        //then
        then(thrown)
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageContaining("Something wrong happened")
    }

    @Test
    fun `test get clients coach plus clients available for a coach`() {

        val uuid = UUID.fromString("e59343bc-6563-4488-a77e-112e886c57ae")

        val client0 = ClientDtoBuilder.maker().but(with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.default())).make()
        val client1 = ClientDtoBuilder.maker().but(with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.default())).make()
        val client2 = ClientDtoBuilder.maker().but(with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.default()),
            with(ClientDtoMaker.clientStatus, ClientStatusDto.ACCEPTED)).make()
        val client3 = ClientDtoBuilder.maker().but(with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.default()),
            with(ClientDtoMaker.clientStatus, ClientStatusDto.ACCEPTED)).make()
        val client4 = ClientDtoBuilder.maker().but(with(ClientDtoMaker.loginInfo, LoginInfoDtoBuilder.default()),
            with(ClientDtoMaker.clientStatus, ClientStatusDto.PENDING)).make()
        val coach = CoachDtoBuilder.maker()
            .but(with(CoachDtoMaker.listOfClients, setOf(client0, client1, client2, client3, client4)),
                with(CoachDtoMaker.identifier, uuid)).make()

        every { coachService.getCoachWithClientsAvailable(uuid) } returns coach

        val response = classToTest.getClientsCoachPlusClientsAvailable(uuid.toString())

        then(response.identifier).isEqualTo(uuid)
        then(response.clientsCoach).isNotEmpty
        then(response.clientsCoach).hasSize(5)
        then(response.clientsCoach.filter { ClientStatusDto.ACCEPTED.name == it.status }).hasSize(2)
        then(response.clientsCoach.filter { ClientStatusDto.PENDING.name == it.status }).hasSize(1)
        then(response.clientsCoach.filter { ClientStatusDto.AVAILABLE.name == it.status }).hasSize(2)

    }

    @Test
    fun `test get clients for a coach invalid uuid format`() {

        // given
        val uuid = "INVALID"

        // when
        val thrown = catchThrowable { classToTest.getClientsCoach(uuid) }

        //then
        then(thrown)
            .isInstanceOf(RestInvalidRequestException::class.java)
            .hasMessageContaining("Invalid UUID string: INVALID")
    }

    @Test
    fun `test get clients coach plus clients available for a coach invalid uuid format`() {

        // given
        val uuid = "INVALID"

        // when
        val thrown = catchThrowable { classToTest.getClientsCoach(uuid) }

        //then
        then(thrown)
            .isInstanceOf(RestInvalidRequestException::class.java)
            .hasMessageContaining("Invalid UUID string: INVALID")
    }

}