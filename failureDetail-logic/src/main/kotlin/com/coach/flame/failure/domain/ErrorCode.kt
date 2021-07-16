package com.coach.flame.failure.domain

enum class ErrorCode(
    val code: Int,
    val description: String,
) {
    //Request
    CODE_1000(1000, "Request unexpected problem"),
    CODE_1001(1001, "Invalid request"),
    CODE_1002(1002, "Authentication failed"),

    //Customer
    CODE_2000(2000, "Customer unexpected problem"),
    CODE_2001(2001, "Customer not found"),
    CODE_2002(2002, "Customer registration duplicated"),
    CODE_2003(2003, "Customer username or password invalid"),
    CODE_2004(2004, "Customer invalid type"),
    CODE_2005(2005, "Customer wrong registration key"),
    CODE_2006(2006, "Customer registration expiration date"),
    CODE_2007(2007, "Customer registration invalid email"),

    //Appointment
    CODE_2100(2100, "Appointment unexpected problem"),
    CODE_2101(2101, "Appointment not found"),
    CODE_2102(2102, "Appointment delete failed"),

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

    //Measures
    CODE_6000(6000, "Measure unexpected problem"),
    CODE_6001(6001, "Measure not found"),

    //Mail
    CODE_7000(7000, "Email unexpected problem"),

    CODE_9999(9999, "Unexpected problem"),

}


