package com.coach.flame.jpa.repository

import com.coach.flame.jpa.entity.Client
import org.springframework.data.jpa.repository.JpaRepository

interface ClientRepository : JpaRepository<Client, Long>