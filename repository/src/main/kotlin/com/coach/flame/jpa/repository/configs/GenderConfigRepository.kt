package com.coach.flame.jpa.repository.configs

import com.coach.flame.jpa.entity.CountryConfig
import com.coach.flame.jpa.entity.GenderConfig
import com.coach.flame.jpa.repository.cache.CacheLoaderRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface GenderConfigRepository : JpaRepository<GenderConfig, Long>, CacheLoaderRepository<GenderConfig> {

    @Query("select c from GenderConfig c " +
            "where c.genderCode = :key")
    override fun findByKey(@Param("key") key: String): GenderConfig?

    @Query("select c from GenderConfig c")
    override fun findAll(): List<GenderConfig>
}
