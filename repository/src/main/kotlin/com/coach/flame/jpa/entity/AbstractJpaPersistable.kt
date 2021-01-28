package com.coach.flame.jpa.entity

import com.google.common.base.MoreObjects
import org.springframework.data.util.ProxyUtils
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class AbstractJpaPersistable<T: Serializable> {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private var id: T? = null

    fun getId(): T? {
        return id
    }

    override fun equals(other: Any?): Boolean {
        other ?: return false

        if (this === other) return true
        if (javaClass != ProxyUtils.getUserClass(other)) return false

        other as AbstractJpaPersistable<*>

        return if (null == id) false else id == other.id
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString() = MoreObjects.toStringHelper(this)
        .add("id", id)
        .toString()

}