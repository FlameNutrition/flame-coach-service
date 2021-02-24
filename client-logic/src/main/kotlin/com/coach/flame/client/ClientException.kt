package com.coach.flame.client

import com.coach.flame.failure.HttpStatus
import com.coach.flame.failure.Status
import com.coach.flame.failure.exception.BusinessException

@Status(httpStatus = HttpStatus.NOT_FOUND)
class ClientNotFound : BusinessException {
    constructor(message: String, ex: Exception?) : super(message, ex)
    constructor(message: String) : super(message)
}