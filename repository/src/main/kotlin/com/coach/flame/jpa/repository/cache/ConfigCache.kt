package com.coach.flame.jpa.repository.cache

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit

class ConfigCache<Entity>(
    expirationTime: Long,
    expirationUnit: TimeUnit,
    repository: CacheLoaderRepository<Entity>,
) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(ConfigCache::class.java)
    }

    private val cache: LoadingCache<String, Entity> = CacheBuilder
        .newBuilder()
        .expireAfterWrite(expirationTime, expirationUnit)
        .build(Loader(repository))

    fun getValue(key: String): Optional<Entity> {

        try {
            return Optional.ofNullable(this.cache.get(key))
        } catch (ex: Exception) {
            LOGGER.warn("opr='getValue', msg='key doesn't exist inside cache', key={}", key, ex)
        }

        return Optional.empty()
    }

    private class Loader<Entity>(
        private val cacheLoaderRepository: CacheLoaderRepository<Entity>,
    ) : CacheLoader<String, Entity>() {

        override fun load(key: String): Entity {

            val customerType = cacheLoaderRepository.findByKey(key)

            checkNotNull(customerType) { "customer type can not be null" }

            LOGGER.debug("opr='load', msg='load key inside cache', key={}, value={}", key, customerType)

            return customerType
        }

    }

}


