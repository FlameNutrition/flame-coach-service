package com.coach.flame.jpa.entity

import com.coach.flame.domain.CustomerTypeDto
import org.springframework.data.jpa.domain.AbstractPersistable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "Client_Type")
class ClientType(

    @Column(nullable = false)
    val type: String,

    ) : AbstractPersistable<Long>(), ConfigKey {

    override fun configKey(): String {
        return type
    }

    fun toDto(): CustomerTypeDto {
        return when {
            CustomerTypeDto.CLIENT.name == this.type -> CustomerTypeDto.CLIENT
            CustomerTypeDto.COACH.name == this.type -> CustomerTypeDto.COACH
            else -> CustomerTypeDto.UNKNOWN
        }
    }

    companion object {
        fun CustomerTypeDto.toClientType(): ClientType {
            val clientType = ClientType(name)
            //FIXME: This should be reviews otherwise database never can change the ids
            clientType.id = when {
                (CustomerTypeDto.COACH == this) -> 1
                (CustomerTypeDto.CLIENT == this) -> 2
                else -> 0
            }
            return clientType
        }
    }

}
