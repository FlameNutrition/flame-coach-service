package com.coach.flame.dailyTask

import com.coach.flame.failure.HttpStatus
import com.coach.flame.failure.Status
import com.coach.flame.failure.exception.BusinessException

@Status(httpStatus = HttpStatus.NOT_FOUND)
class ClientNotFound : BusinessException {
    constructor(message: String, ex: Exception?) : super(message, ex)
    constructor(ex: Exception) : super(ex)
}

@Status(httpStatus = HttpStatus.NOT_FOUND)
class DailyTaskNotFound : BusinessException {
    constructor(message: String, ex: Exception?) : super(message, ex)
    constructor(message: String) : super(message)
}

@Status(httpStatus = HttpStatus.BAD_REQUEST)
class DailyTaskMissingSave : BusinessException {
    constructor(message: String, ex: Exception?) : super(message, ex)
}

@Status(httpStatus = HttpStatus.BAD_REQUEST)
class DailyTaskMissingDelete : BusinessException {
    constructor(message: String) : super(message)
}