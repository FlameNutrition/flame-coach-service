package com.coach.flame.dailyTask

import com.coach.flame.failure.HttpStatus
import com.coach.flame.failure.Status
import com.coach.flame.failure.exception.BusinessException

@Status(httpStatus = HttpStatus.NOT_FOUND)
class ClientNotFoundException : BusinessException {
    constructor(message: String, ex: Exception) : super(message, ex)
    constructor(message: String) : super(message)
}

@Status(httpStatus = HttpStatus.NOT_FOUND)
class DailyTaskNotFoundException : BusinessException {
    constructor(message: String, ex: Exception) : super(message, ex)
    constructor(message: String) : super(message)
}

@Status(httpStatus = HttpStatus.BAD_REQUEST)
class DailyTaskMissingSaveException : BusinessException {
    constructor(message: String, ex: Exception) : super(message, ex)
}

@Status(httpStatus = HttpStatus.BAD_REQUEST)
class DailyTaskMissingDeleteException : BusinessException {
    constructor(message: String) : super(message)
}