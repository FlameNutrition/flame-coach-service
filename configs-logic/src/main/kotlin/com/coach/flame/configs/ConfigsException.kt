package com.coach.flame.configs

import com.coach.flame.failure.HttpStatus
import com.coach.flame.failure.Status
import com.coach.flame.failure.domain.ErrorCode
import com.coach.flame.failure.exception.BusinessException

@Status(httpStatus = HttpStatus.INTERNAL_SERVER_ERROR)
class UnexpectedConfigException : BusinessException {
    constructor(message: String, ex: Exception) : super(ErrorCode.CODE_5001, message, ex)
    constructor(message: String) : super(ErrorCode.CODE_5001, message)
}
