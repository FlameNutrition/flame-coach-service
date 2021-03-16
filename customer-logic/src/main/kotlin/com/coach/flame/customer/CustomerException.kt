package com.coach.flame.customer

import com.coach.flame.failure.HttpStatus
import com.coach.flame.failure.Status
import com.coach.flame.failure.exception.BusinessException

@Status(httpStatus = HttpStatus.NOT_FOUND)
class CustomerNotFoundException : BusinessException {
    constructor(message: String, ex: Exception) : super(message, ex)
    constructor(message: String) : super(message)
}

@Status(httpStatus = HttpStatus.BAD_REQUEST)
class CustomerRegisterDuplicateException : BusinessException {
    constructor(message: String, ex: Exception) : super(message, ex)
    constructor(message: String) : super(message)
}

@Status(httpStatus = HttpStatus.BAD_REQUEST)
class CustomerUsernameOrPasswordException : BusinessException {
    constructor(message: String, ex: Exception) : super(message, ex)
    constructor(message: String) : super(message)
}

@Status(httpStatus = HttpStatus.BAD_REQUEST)
class EnrollmentProcessException : BusinessException {
    constructor(message: String, ex: Exception) : super(message, ex)
    constructor(message: String) : super(message)
}

@Status(httpStatus = HttpStatus.INTERNAL_SERVER_ERROR)
class CustomerRetrieveException : BusinessException {
    constructor(message: String, ex: Exception) : super(message, ex)
    constructor(message: String) : super(message)
}

@Status(httpStatus = HttpStatus.INTERNAL_SERVER_ERROR)
class CustomerRegisterException : BusinessException {
    constructor(message: String, ex: Exception) : super(message, ex)
    constructor(message: String) : super(message)
}

@Status(httpStatus = HttpStatus.INTERNAL_SERVER_ERROR)
class CustomerNewSessionException : BusinessException {
    constructor(message: String, ex: Exception) : super(message, ex)
    constructor(message: String) : super(message)
}