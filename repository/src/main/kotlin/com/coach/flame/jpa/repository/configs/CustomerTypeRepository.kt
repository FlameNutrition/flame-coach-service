package com.coach.flame.jpa.repository.configs

import com.coach.flame.jpa.entity.ClientType
import com.coach.flame.jpa.repository.cache.CacheLoaderRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CustomerTypeRepository : JpaRepository<ClientType, Long>, CacheLoaderRepository<ClientType> {

    @Query("select ct from ClientType ct " +
            "where ct.type = :key")
    override fun findByKey(@Param("key") key: String): ClientType?

    @Query("select ct from ClientType ct")
    override fun findAll(): List<ClientType>
}
