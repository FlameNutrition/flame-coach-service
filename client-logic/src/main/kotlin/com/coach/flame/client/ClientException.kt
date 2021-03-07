package com.coach.flame.client

import com.coach.flame.failure.HttpStatus
import com.coach.flame.failure.Status
import com.coach.flame.failure.exception.BusinessException

@Status(httpStatus = HttpStatus.NOT_FOUND)
class ClientNotFoundException : BusinessException {
    constructor(message: String, ex: Exception) : super(message, ex)
    constructor(message: String) : super(message)
}

@Status(httpStatus = HttpStatus.BAD_REQUEST)
class ClientRegisterDuplicateException : BusinessException {
    constructor(message: String, ex: Exception) : super(message, ex)
    constructor(message: String) : super(message)
}

@Status(httpStatus = HttpStatus.BAD_REQUEST)
class ClientUsernameOrPasswordException : BusinessException {
    constructor(message: String, ex: Exception) : super(message, ex)
    constructor(message: String) : super(message)
}

@Status(httpStatus = HttpStatus.INTERNAL_SERVER_ERROR)
class ClientRegisterException : BusinessException {
    constructor(message: String, ex: Exception) : super(message, ex)
    constructor(message: String) : super(message)
}