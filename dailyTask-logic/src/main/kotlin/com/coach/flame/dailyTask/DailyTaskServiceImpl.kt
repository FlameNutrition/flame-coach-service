package com.coach.flame.dailyTask

import com.coach.flame.dailyTask.filter.Filter
import com.coach.flame.domain.DailyTaskDto
import com.coach.flame.jpa.entity.DailyTask
import com.coach.flame.jpa.repository.ClientRepository
import com.coach.flame.jpa.repository.CoachRepository
import com.coach.flame.jpa.repository.DailyTaskRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.*

@Service
class DailyTaskServiceImpl(
    private val dailyTaskRepository: DailyTaskRepository,
    private val clientRepository: ClientRepository,
    private val coachRepository: CoachRepository,
) : DailyTaskService {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(DailyTaskServiceImpl::class.java)
    }

    @Transactional(readOnly = true)
    override fun getDailyTaskById(taskId: Long): DailyTaskDto {

        LOGGER.info("opr='getDailyTaskById', msg='Get task by ID', taskId=$taskId")

        val dailyTask = dailyTaskRepository.findById(taskId)

        if (dailyTask.isEmpty) {
            throw DailyTaskNotFoundException("Could not find any daily task with id: $taskId.")
        }

        return dailyTask.get().toDto()

    }

    @Transactional(readOnly = true)
    override fun getDailyTasksByClient(uuid: UUID): Set<DailyTaskDto> {

        LOGGER.info("opr='getDailyTasksByClient', msg='Get all tasks for a client', uuid=$uuid")

        val dailyTasks = dailyTaskRepository.findAllByClient(uuid)

        if (dailyTasks.isEmpty) {
            return setOf()
        }

        return dailyTasks
            .get()
            .map { it.toDto() }
            .toSet()

    }

    @Transactional(readOnly = true)
    override fun getDailyTasksUsingFilters(filters: Set<Filter>): Set<DailyTaskDto> {

        LOGGER.info("opr='getDailyTasksUsingFilters', msg='Get daily task using filters', numOfFilters={}",
            filters.size)

        var criterias: Specification<DailyTask>? = null

        filters.forEach {
            LOGGER.info("opr='getDailyTasksUsingFilters', msg='Applying filter', filterName={}", it.javaClass.name)
            criterias = criterias?.and(it.getFilter()) ?: it.getFilter()
        }

        return dailyTaskRepository.findAll(criterias)
            .map { it.toDto() }
            .toSet()

    }

    @Transactional
    override fun createDailyTask(dailyTask: DailyTaskDto, toDttm: LocalDate): Set<DailyTaskDto> {

        val dailyTaskList = mutableSetOf<DailyTaskDto>()

        var nextDttm = dailyTask.date
        var nextDailyTask = dailyTask

        do {
            dailyTaskList.add(createDailyTask(nextDailyTask))

            nextDttm = nextDttm.plusDays(1)
            nextDailyTask = dailyTask
                .copy(identifier = UUID.randomUUID(),
                    date = nextDttm)
        } while (nextDttm != toDttm.plusDays(1))

        return dailyTaskList
    }

    @Transactional
    override fun createDailyTask(dailyTask: DailyTaskDto): DailyTaskDto {

        LOGGER.info("opr='createDailyTask', msg='Creating the following task', dailyTask={}", dailyTask.identifier)

        checkNotNull(dailyTask.coachIdentifier) { "coachIdentifier is a mandatory parameter" }
        checkNotNull(dailyTask.clientIdentifier) { "clientIdentifier is a mandatory parameter" }

        val coach = coachRepository.findByUuid(dailyTask.coachIdentifier!!)
            ?: run {
                LOGGER.warn("opr='createDailyTask', msg='Invalid coach token', coachToken={}",
                    dailyTask.coachIdentifier)
                throw CustomerNotFoundException("Didn't find any coach with this identifier, please check the coach identifier.")
            }

        val client = clientRepository.findByUuid(dailyTask.clientIdentifier!!)
            ?: run {
                LOGGER.warn("opr='createDailyTask', msg='Invalid client identifier', clientIdentifier={}",
                    dailyTask.clientIdentifier)
                throw CustomerNotFoundException("Didn't find any client with this identifier, please check the client identifier.")
            }

        val newDailyTask = DailyTask(
            uuid = dailyTask.identifier,
            name = dailyTask.name,
            description = dailyTask.description,
            date = dailyTask.date,
            ticked = false,
            createdBy = coach,
            client = client
        )

        val entity = dailyTaskRepository.save(newDailyTask)

        return entity.toDto()

    }

    @Transactional
    override fun updateDailyTask(dailyTask: DailyTaskDto): DailyTaskDto {

        LOGGER.info("opr='updateDailyTask', msg='Updating the following task', dailyTask={}", dailyTask.identifier)

        val task = dailyTaskRepository.findByUuid(dailyTask.identifier)
            ?: run {
                LOGGER.warn("opr='updateDailyTask', msg='Daily Task doesn't exist', dailyTask={}",
                    dailyTask.identifier)
                throw DailyTaskNotFoundException("Daily task not found, please check the identifier.")
            }

        //Update values
        task.name = dailyTask.name
        task.description = dailyTask.description
        task.date = dailyTask.date
        task.ticked = dailyTask.ticked

        val entity = dailyTaskRepository.save(task)

        return entity.toDto()

    }

    @Transactional
    override fun deleteDailyTask(uuid: UUID) {

        LOGGER.info("opr='deleteDailyTask', msg='Deleting the following task', uuid=$uuid")

        if (dailyTaskRepository.deleteByUuid(uuid) == 0) {
            throw DailyTaskMissingDeleteException("Didn't find the following uuid task: $uuid.")
        }
    }

}
