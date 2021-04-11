package com.coach.flame.dailyTask

import com.coach.flame.failure.HttpStatus
import com.coach.flame.failure.Status
import com.coach.flame.failure.domain.ErrorCode
import com.coach.flame.failure.exception.BusinessException

@Status(httpStatus = HttpStatus.NOT_FOUND)
class CustomerNotFoundException : BusinessException {
    constructor(message: String) : super(ErrorCode.CODE_2001, message)
}

@Status(httpStatus = HttpStatus.NOT_FOUND)
class DailyTaskNotFoundException : BusinessException {
    constructor(message: String, ex: Exception) : super(ErrorCode.CODE_4001, message, ex)
    constructor(message: String) : super(ErrorCode.CODE_4001, message)
}

@Status(httpStatus = HttpStatus.BAD_REQUEST)
class DailyTaskMissingDeleteException : BusinessException {
    constructor(message: String) : super(ErrorCode.CODE_4002, message)
}
