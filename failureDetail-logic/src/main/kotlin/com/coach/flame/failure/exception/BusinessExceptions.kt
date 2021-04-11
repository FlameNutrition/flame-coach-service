package com.coach.flame.failure.exception

import com.coach.flame.failure.domain.ErrorCode

open class BusinessException : RuntimeException {

    var errorCode: ErrorCode

    constructor(errorCode: ErrorCode, message: String, ex: Exception) : super(message, ex) {
        this.errorCode = errorCode
    }

    constructor(errorCode: ErrorCode, message: String) : super(message) {
        this.errorCode = errorCode
    }
}
