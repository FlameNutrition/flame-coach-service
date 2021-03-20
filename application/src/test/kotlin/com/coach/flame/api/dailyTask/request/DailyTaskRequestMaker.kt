package com.coach.flame.api.dailyTask.request

import com.github.javafaker.Faker
import com.natpryce.makeiteasy.Instantiator
import com.natpryce.makeiteasy.MakeItEasy
import com.natpryce.makeiteasy.Maker
import com.natpryce.makeiteasy.Property
import com.natpryce.makeiteasy.Property.newProperty
import java.time.LocalDate

object DailyTaskRequestBuilder {

    private val MAKER: Maker<DailyTaskRequest> = MakeItEasy.an(DailyTaskRequestMaker.DailyTaskRequest)

    fun maker(): Maker<DailyTaskRequest> {
        return MAKER
    }

    fun default(): DailyTaskRequest {
        return maker().make()
    }

}

class DailyTaskRequestMaker {

    companion object {

        private val fake = Faker()
        val name: Property<DailyTaskRequest, String?> = newProperty()
        val description: Property<DailyTaskRequest, String?> = newProperty()
        val date: Property<DailyTaskRequest, String?> = newProperty()
        val ticked: Property<DailyTaskRequest, Boolean?> = newProperty()
        val toDate: Property<DailyTaskRequest, String?> = newProperty()

        val DailyTaskRequest: Instantiator<DailyTaskRequest> = Instantiator {
            DailyTaskRequest(
                taskName = it.valueOf(name, "Eat  $fake.food().fruit()"),
                taskDescription = it.valueOf(description, fake.friends().quote()),
                date = it.valueOf(date, LocalDate.now().toString()),
                ticked = it.valueOf(ticked, null as Boolean?),
                toDate = it.valueOf(toDate, null as String?),
            )
        }
    }

}