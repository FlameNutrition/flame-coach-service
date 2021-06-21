package com.coach.flame.exception

import com.coach.flame.failure.HttpStatus
import com.coach.flame.failure.Status
import com.coach.flame.failure.domain.ErrorCode
import com.coach.flame.failure.exception.BusinessException

@Status(httpStatus = HttpStatus.INTERNAL_SERVER_ERROR)
open class InternalServerException(errorCode: ErrorCode, message: String, ex: Exception) :
    BusinessException(errorCode, message, ex)

@Status(httpStatus = HttpStatus.INTERNAL_SERVER_ERROR)
open class RestException : InternalServerException {
    constructor(errorCode: ErrorCode, message: String, ex: Exception) : super(errorCode, message, ex)
}

@Status(httpStatus = HttpStatus.UNAUTHORIZED)
open class RestAuthenticationException : RestException {
    constructor(message: String, ex: Exception) : super(ErrorCode.CODE_1002, message, ex)
}

@Status(httpStatus = HttpStatus.BAD_REQUEST)
open class RestInvalidRequestException : RestException {
    constructor(message: String, ex: Exception) : super(ErrorCode.CODE_1001, message, ex)
}
