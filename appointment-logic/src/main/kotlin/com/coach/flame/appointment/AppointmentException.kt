package com.coach.flame.appointment

import com.coach.flame.failure.HttpStatus
import com.coach.flame.failure.Status
import com.coach.flame.failure.domain.ErrorCode
import com.coach.flame.failure.exception.BusinessException

@Status(httpStatus = HttpStatus.NOT_FOUND)
class AppointmentNotFoundException : BusinessException {
    constructor(message: String, ex: Exception) : super(ErrorCode.CODE_2101, message, ex)
    constructor(message: String) : super(ErrorCode.CODE_2101, message)
}

@Status(httpStatus = HttpStatus.NOT_FOUND)
class AppointmentMissingDeleteException : BusinessException {
    constructor(message: String) : super(ErrorCode.CODE_2102, message)
}
