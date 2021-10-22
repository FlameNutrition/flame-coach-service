package com.coach.flame.socialnetworking

import com.coach.flame.domain.ChatDto
import com.coach.flame.domain.MessageDto
import org.springframework.stereotype.Service
import java.util.*

@Service
class ChatServiceImpl : ChatService {
    override fun sendMessage(message: MessageDto): MessageDto {
        TODO("Not yet implemented")
    }

    override fun getChat(client: UUID, coach: UUID): ChatDto {
        TODO("Not yet implemented")
    }
}
