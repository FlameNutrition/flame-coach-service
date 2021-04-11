package com.coach.flame.failure.domain

enum class ErrorCode(
    val code: Int,
    val description: String,
) {
    //Request
    CODE_1000(1000, "Request unexpected problem"),
    CODE_1001(1001, "Invalid request"),

    //Customer
    CODE_2000(2000, "Customer unexpected problem"),
    CODE_2001(2001, "Customer not found"),
    CODE_2002(2002, "Customer registration duplicated"),
    CODE_2003(2003, "Customer username or password invalid"),
    CODE_2004(2004, "Customer invalid type"),

    //Enrollment Process
    CODE_3000(3000, "Enrollment process unexpected problem"),
    CODE_3001(3001, "Client already has a coach assigned"),
    CODE_3002(3002, "Enrollment process didn't start or client already has a coach assigned"),

    //Daily Tasks
    CODE_4000(4000, "Daily task unexpected problem"),
    CODE_4001(4001, "Daily task not found"),
    CODE_4002(4002, "Daily task delete failed"),

    //Configs
    CODE_5000(5000, "Configs unexpected problem"),
    CODE_5001(5001, "Config is not present"),

    CODE_9999(9999, "Unexpected problem"),

}


