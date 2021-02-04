package com.coach.flame.failure

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Status(val httpStatus: HttpStatus)
