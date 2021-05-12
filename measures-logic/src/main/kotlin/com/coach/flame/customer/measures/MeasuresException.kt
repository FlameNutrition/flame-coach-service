package com.coach.flame.customer.measures

import com.coach.flame.failure.HttpStatus
import com.coach.flame.failure.Status
import com.coach.flame.failure.domain.ErrorCode
import com.coach.flame.failure.exception.BusinessException

@Status(httpStatus = HttpStatus.NOT_FOUND)
class MeasureNotFoundException : BusinessException {
    constructor(message: String, ex: Exception) : super(ErrorCode.CODE_6001, message, ex)
    constructor(message: String) : super(ErrorCode.CODE_6001, message)
}

@Status(httpStatus = HttpStatus.INTERNAL_SERVER_ERROR)
class MeasureException : BusinessException {
    constructor(errorCode: ErrorCode, message: String, ex: Exception) : super(errorCode, message, ex)
    constructor(errorCode: ErrorCode, message: String) : super(errorCode, message)
}
