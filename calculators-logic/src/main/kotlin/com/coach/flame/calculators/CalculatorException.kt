package com.coach.flame.calculators

import com.coach.flame.failure.HttpStatus
import com.coach.flame.failure.Status
import com.coach.flame.failure.domain.ErrorCode
import com.coach.flame.failure.exception.BusinessException

@Status(httpStatus = HttpStatus.NOT_IMPLEMENTED)
class UnsupportedFormulaException : BusinessException {
    constructor(message: String, ex: Exception) : super(ErrorCode.CODE_0001, message, ex)
    constructor(message: String) : super(ErrorCode.CODE_0001, message)
}

