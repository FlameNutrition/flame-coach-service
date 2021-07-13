package com.coach.flame.jpa

import com.coach.flame.jpa.entity.*
import com.coach.flame.jpa.entity.maker.*
import com.coach.flame.jpa.repository.*
import com.natpryce.makeiteasy.MakeItEasy.an
import com.natpryce.makeiteasy.MakeItEasy.with
import com.natpryce.makeiteasy.Maker
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

abstract class AbstractHelperTest {

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    //https://stackoverflow.com/questions/64459905/persist-many-to-many-relationship-with-datajpatest-and-h2-database-not-working
    @PersistenceContext
    protected lateinit var entityManager: EntityManager

    protected val clientMaker: Maker<Client> = an(ClientMaker.Client)
    protected val clientTypeMaker: Maker<ClientType> = an(ClientTypeMaker.ClientType)
    protected val userMaker: Maker<User> = an(UserMaker.User)
    protected val userSessionMaker: Maker<UserSession> = an(UserSessionMaker.UserSession)
    protected val dailyTaskMaker: Maker<DailyTask> = an(DailyTaskMaker.DailyTask)
    protected val coachMaker: Maker<Coach> = an(CoachMaker.Coach)

    protected lateinit var clientType: ClientType
    protected lateinit var coachType: ClientType

    @Autowired
    private lateinit var clientTypeRepository: ClientTypeRepository

    @Autowired
    private lateinit var clientRepository: ClientRepository

    @Autowired
    private lateinit var userSessionRepository: UserSessionRepository

    @Autowired
    private lateinit var dailyTaskRepository: DailyTaskRepository

    @Autowired
    private lateinit var coachRepository: CoachRepository

    @Autowired
    private lateinit var registrationInviteRepository: RegistrationInviteRepository

    @BeforeEach
    internal open fun setUp() {
        coachType = clientTypeRepository.saveAndFlush(clientTypeMaker
            .but(with(ClientTypeMaker.type, "COACH"))
            .make())
        clientType = clientTypeRepository.saveAndFlush(clientTypeMaker
            .but(with(ClientTypeMaker.type, "CLIENT"))
            .make())
    }

    fun getClientTypeRepository(): ClientTypeRepository {
        return clientTypeRepository
    }

    fun getClientRepository(): ClientRepository {
        return clientRepository
    }

    fun getUserSessionRepositoryRepository(): UserSessionRepository {
        return userSessionRepository
    }

    fun getDailyTaskRepository(): DailyTaskRepository {
        return dailyTaskRepository
    }

    fun getCoachRepository(): CoachRepository {
        return coachRepository
    }

    fun getRegistrationInviteRepository(): RegistrationInviteRepository {
        return registrationInviteRepository
    }

}
