package com.coach.flame.base64

import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class Base64Test {

    @Test
    fun `test encode string value`() {
        then(Base64.encode("2021-05-20T10:10:10_test@gmail.com")).isEqualTo("MjAyMS0wNS0yMFQxMDoxMDoxMF90ZXN0QGdtYWlsLmNvbQ==")
    }

    @Test
    fun `test decode string encoded`() {
        then(Base64.decode("MjAyMS0wNS0yMFQxMDoxMDoxMF90ZXN0QGdtYWlsLmNvbQ==")).isEqualTo("2021-05-20T10:10:10_test@gmail.com")
    }

}
