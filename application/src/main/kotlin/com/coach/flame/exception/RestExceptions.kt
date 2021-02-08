package com.coach.flame.exception

import com.coach.flame.failure.HttpStatus
import com.coach.flame.failure.Status
import com.coach.flame.failure.exception.BusinessException

@Status(httpStatus = HttpStatus.BAD_REQUEST)
class RestInvalidRequest : BusinessException {
    constructor(message: String, ex: Exception?) : super(message, ex)
    constructor(message: String) : super(message)
    constructor(ex: Exception) : super(ex)
}