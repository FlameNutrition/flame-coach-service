package com.coach.flame.failure.exception

import com.coach.flame.failure.HttpStatus
import com.coach.flame.failure.Status
import com.coach.flame.failure.domain.ErrorCode
import com.coach.flame.failure.exception.BusinessException

@Status(httpStatus = HttpStatus.NOT_FOUND)
class CustomerNotFoundException : BusinessException {
    constructor(message: String, ex: Exception) : super(ErrorCode.CODE_2001, message, ex)
    constructor(message: String) : super(ErrorCode.CODE_2001, message)
}

@Status(httpStatus = HttpStatus.BAD_REQUEST)
class CustomerRegisterDuplicateException : BusinessException {
    constructor(message: String, ex: Exception) : super(ErrorCode.CODE_2002, message, ex)
    constructor(message: String) : super(ErrorCode.CODE_2002, message)
}

@Status(httpStatus = HttpStatus.BAD_REQUEST)
class CustomerRegisterWrongRegistrationKey : BusinessException {
    constructor(message: String, ex: Exception) : super(ErrorCode.CODE_2005, message, ex)
    constructor(message: String) : super(ErrorCode.CODE_2005, message)
}

@Status(httpStatus = HttpStatus.BAD_REQUEST)
class CustomerRegisterExpirationDate : BusinessException {
    constructor(message: String, ex: Exception) : super(ErrorCode.CODE_2006, message, ex)
    constructor(message: String) : super(ErrorCode.CODE_2006, message)
}

@Status(httpStatus = HttpStatus.BAD_REQUEST)
class CustomerRegisterInvalidEmail : BusinessException {
    constructor(message: String, ex: Exception) : super(ErrorCode.CODE_2007, message, ex)
    constructor(message: String) : super(ErrorCode.CODE_2007, message)
}

@Status(httpStatus = HttpStatus.BAD_REQUEST)
class CustomerUsernameOrPasswordException : BusinessException {
    constructor(message: String, ex: Exception) : super(ErrorCode.CODE_2003, message, ex)
    constructor(message: String) : super(ErrorCode.CODE_2003, message)
}

@Status(httpStatus = HttpStatus.BAD_REQUEST)
class EnrollmentProcessException : BusinessException {
    constructor(errorCode: ErrorCode, message: String, ex: Exception) : super(errorCode, message, ex)
    constructor(errorCode: ErrorCode, message: String) : super(errorCode, message)
}

@Status(httpStatus = HttpStatus.INTERNAL_SERVER_ERROR)
class CustomerException : BusinessException {
    constructor(errorCode: ErrorCode, message: String, ex: Exception) : super(errorCode, message, ex)
    constructor(errorCode: ErrorCode, message: String) : super(errorCode, message)
}

@Status(httpStatus = HttpStatus.INTERNAL_SERVER_ERROR)
class MailException : BusinessException {
    constructor(message: String, ex: Exception) : super(ErrorCode.CODE_7000, message, ex)
}

@Status(httpStatus = HttpStatus.INTERNAL_SERVER_ERROR)
class SecurityException : BusinessException {
    constructor(message: String, ex: Exception) : super(ErrorCode.CODE_9999, message, ex)
    constructor(message: String) : super(ErrorCode.CODE_9999, message)
}
