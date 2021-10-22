package com.coach.flame.api.networking

import com.coach.flame.api.networking.request.ChatRequest
import com.coach.flame.api.networking.request.MessageRequest
import com.coach.flame.api.networking.response.ChatResponse
import com.coach.flame.api.networking.response.MessageResponse
import java.util.*

/**
 * //TODO: Add description
 *
 * @author Nuno Bento
 */
interface ChatAPI {

    /**
     * Send message using information at [MessageRequest]
     *
     * @param request information to send the message
     *
     * @return message if successfully sent otherwise returns an exception
     */
    fun sendMessage(request: MessageRequest): MessageResponse

    /**
     * Get the full chat between client and coach
     *
     * @param request with the information to get the chat between client and coach
     *
     * @return chat ordered between client and coach otherwise and exception
     */
    fun getChat(request: ChatRequest): ChatResponse

}
