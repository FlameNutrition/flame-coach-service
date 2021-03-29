package com.coach.flame.jpa.repository.configs

import com.coach.flame.jpa.entity.CountryConfig
import com.coach.flame.jpa.repository.cache.CacheLoaderRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CountryConfigRepository : JpaRepository<CountryConfig, Long>, CacheLoaderRepository<CountryConfig> {

    @Query("select c from CountryConfig c " +
            "where c.countryCode = :key")
    override fun findByKey(@Param("key") key: String): CountryConfig?

    @Query("select c from CountryConfig c")
    override fun findAll(): List<CountryConfig>
}
