package com.coach.flame.socialnetworking

import com.coach.flame.domain.ChatDto
import com.coach.flame.domain.MessageDto
import java.util.*

/**
 * Use this service to expose methods for the chat, e.g. to send a message.
 *
 * @author Nuno Bento
 */
interface ChatService {

    /**
     * Send a message
     *
     * @param message information, such as, from, to, owner and content of the message
     *
     * @return message dto instance with the identifier otherwise an exception
     */
    fun sendMessage(message: MessageDto): MessageDto

    /**
     * Get entire chat between client and coach
     *
     * @param client identification
     * @param coach identification
     *
     * @return chat with all messages between client and coach otherwise an exception
     */
    fun getChat(client: UUID, coach: UUID): ChatDto

}
