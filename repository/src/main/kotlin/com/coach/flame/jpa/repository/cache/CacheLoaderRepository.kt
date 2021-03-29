package com.coach.flame.jpa.repository.cache

interface CacheLoaderRepository<T> {

    fun findByKey(key: String): T?

    fun findAll(): List<T>

}
