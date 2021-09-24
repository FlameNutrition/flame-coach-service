package com.coach.flame.testing.component.base.mock

import io.mockk.CapturingSlot

abstract class MockRepository<C, R : Any> {

    protected var mockMethod: String = ""

    protected var mockParams: Map<String, Any> = mapOf()

    protected fun clean() {
        mockMethod = ""
        mockParams = mapOf()
    }

    fun mock(name: String): MockRepository<C, R> = apply { this.mockMethod = name }

    fun params(mapOf: Map<String, Any>): MockRepository<C, R> = apply { this.mockParams = mapOf }

    abstract fun returnsBool(f: () -> Boolean)

    abstract fun returnsMulti(f: () -> List<R?>)

    abstract fun returns(f: () -> R?)

    abstract fun capture(): CapturingSlot<R>

}
