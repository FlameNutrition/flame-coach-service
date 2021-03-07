package com.coach.flame.exception

import com.coach.flame.failure.HttpStatus
import com.coach.flame.failure.Status
import com.coach.flame.failure.exception.BusinessException

@Status(httpStatus = HttpStatus.INTERNAL_SERVER_ERROR)
open class InternalServerException : BusinessException {
    constructor(message: String) : super(message)
}

@Status(httpStatus = HttpStatus.INTERNAL_SERVER_ERROR)
open class RestException : BusinessException {
    constructor(message: String) : super(message)
    constructor(ex: Exception) : super(ex)
}

@Status(httpStatus = HttpStatus.BAD_REQUEST)
class RestInvalidRequestException : RestException {
    constructor(message: String) : super(message)
    constructor(ex: Exception) : super(ex)
}