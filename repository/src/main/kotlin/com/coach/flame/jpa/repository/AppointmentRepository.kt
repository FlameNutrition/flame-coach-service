package com.coach.flame.jpa.repository

import com.coach.flame.jpa.entity.Appointment
import com.coach.flame.jpa.entity.Client
import com.coach.flame.jpa.entity.Coach
import com.coach.flame.jpa.entity.DailyTask
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AppointmentRepository : JpaRepository<Appointment, Long> {

    @Query("select a from Appointment a " +
            "where a.coach.uuid = :uuidCoach and a.client.uuid = :uuidClient and a.delete = false")
    fun findAppointments(@Param("uuidCoach") uuidCoach: UUID, @Param("uuidClient") uuidClient: UUID): List<Appointment>

    @Query("select a from Appointment a " +
            "where a.client.uuid = :uuid and a.delete = false")
    fun findAppointmentsByClient(@Param("uuid") uuid: UUID): List<Appointment>

    @Query("select a from Appointment a " +
            "where a.coach.uuid = :uuid and a.delete = false")
    fun findAppointmentsByCoach(@Param("uuid") uuid: UUID): List<Appointment>

    fun findByUuidAndDeleteFalse(uuid: UUID): Appointment?

}
