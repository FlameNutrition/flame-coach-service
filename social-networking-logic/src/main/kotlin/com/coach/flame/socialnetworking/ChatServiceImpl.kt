package com.coach.flame.socialnetworking

import com.coach.flame.domain.ChatDto
import com.coach.flame.domain.MessageDto
import com.coach.flame.jpa.entity.Message.Companion.toMessage
import com.coach.flame.jpa.repository.MessageRepository
import com.coach.flame.jpa.repository.operations.ClientRepositoryOperation
import com.coach.flame.jpa.repository.operations.CoachRepositoryOperation
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ChatServiceImpl(
    private val clientRepositoryOperation: ClientRepositoryOperation,
    private val coachRepositoryOperation: CoachRepositoryOperation,
    private val messageRepository: MessageRepository
) : ChatService {

    @Transactional
    override fun sendMessage(message: MessageDto): MessageDto {

        val entityMessage = when (message.owner) {
            MessageDto.Owner.CLIENT -> {
                val fromClient = clientRepositoryOperation.getClient(message.from)
                val toCoach = coachRepositoryOperation.getCoach(message.to)
                message.toMessage(toCoach, fromClient)
            }
            MessageDto.Owner.COACH -> {
                val fromCoach = coachRepositoryOperation.getCoach(message.from)
                val toClient = clientRepositoryOperation.getClient(message.to)
                message.toMessage(fromCoach, toClient)
            }
        }

        val persistedMessage = messageRepository.save(entityMessage)

        return persistedMessage.toDto()

    }

    override fun getChat(client: UUID, coach: UUID): ChatDto {
        TODO("Not yet implemented")
    }

}
