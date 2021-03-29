package com.coach.flame.domain

import java.time.LocalDate
import java.util.*

interface Customer {
    val identifier: UUID
    val firstName: String
    val lastName: String
    val birthday: LocalDate?
    val phoneCode: String?
    val phoneNumber: String?
    val country: CountryDto?
    val gender: GenderDto?
    val customerType: CustomerTypeDto
    val loginInfo: LoginInfoDto?
    val registrationDate: LocalDate
}
