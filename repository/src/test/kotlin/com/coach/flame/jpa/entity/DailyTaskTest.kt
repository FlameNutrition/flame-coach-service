package com.coach.flame.jpa.entity

import com.coach.flame.domain.*
import com.coach.flame.domain.maker.*
import com.coach.flame.jpa.entity.DailyTask.Companion.toDailyTask
import com.coach.flame.jpa.entity.maker.*
import com.natpryce.makeiteasy.MakeItEasy.with
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import java.util.*

class DailyTaskTest {

    @Test
    fun `test convert daily task to dto all values`() {

        val dailyTask = DailyTaskBuilder.default()

        val dto = dailyTask.toDto()

        then(dto.id).isEqualTo(dailyTask.id)
        then(dto.identifier).isEqualTo(dailyTask.uuid)
        then(dto.name).isEqualTo(dailyTask.name)
        then(dto.description).isEqualTo(dailyTask.description)
        then(dto.date).isEqualTo(dailyTask.date)
        then(dto.ticked).isEqualTo(dailyTask.ticked)
        then(dto.coachIdentifier).isEqualTo(dailyTask.createdBy.uuid)
        then(dto.clientIdentifier).isEqualTo(dailyTask.client.uuid)
        then(dto.coach).isNotNull
        then(dto.client).isNotNull

    }

    @Test
    fun `test convert dto to entity all values`() {

        val loginInfoDto = LoginInfoDtoBuilder.maker()
            .but(with(LoginInfoDtoMaker.token, UUID.randomUUID()),
                with(LoginInfoDtoMaker.expirationDate, LocalDateTime.now()))
            .make()

        val coach = CoachDtoBuilder.maker()
            .but(with(CoachDtoMaker.loginInfo, loginInfoDto))
            .make()

        val client = ClientDtoBuilder.maker()
            .but(with(ClientDtoMaker.loginInfo, loginInfoDto))
            .make()

        val dailyTaskDto = DailyTaskDtoBuilder.maker()
            .but(with(DailyTaskDtoMaker.coach, coach),
                with(DailyTaskDtoMaker.client, client))
            .make()

        val entity = dailyTaskDto.toDailyTask()

        then(entity.id).isEqualTo(dailyTaskDto.id)
        then(entity.uuid).isEqualTo(dailyTaskDto.identifier)
        then(entity.name).isEqualTo(dailyTaskDto.name)
        then(entity.description).isEqualTo(dailyTaskDto.description)
        then(entity.ticked).isEqualTo(dailyTaskDto.ticked)
        then(entity.createdBy).isNotNull
        then(entity.client).isNotNull

    }

    @Test
    fun `test convert daily task to entity illegal args`() {

        val dailyTaskDto0 = DailyTaskDtoBuilder.maker()
            .but(with(DailyTaskDtoMaker.coach, null as CoachDto?))
            .make()
        val dailyTaskDto1 = DailyTaskDtoBuilder.maker()
            .but(with(DailyTaskDtoMaker.client, null as ClientDto?))
            .make()

        val exception0 = catchThrowable { dailyTaskDto0.toDailyTask() }
        val exception1 = catchThrowable { dailyTaskDto1.toDailyTask() }

        then(exception0).isInstanceOf(IllegalArgumentException::class.java)
        then(exception0).hasMessageContaining("coach can not be null")

        then(exception1).isInstanceOf(IllegalArgumentException::class.java)
        then(exception1).hasMessageContaining("client can not be null")

    }
}
