package com.coach.flame.api.networking

import com.coach.flame.api.APIWrapperException
import com.coach.flame.api.networking.request.ChatRequest
import com.coach.flame.api.networking.request.MessageRequest
import com.coach.flame.api.networking.response.ChatResponse
import com.coach.flame.api.networking.response.MessageResponse
import com.coach.flame.aspect.LoggingRequest
import com.coach.flame.aspect.LoggingResponse
import com.coach.flame.domain.MessageDto
import com.coach.flame.socialnetworking.ChatService
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/socialNetworking/chat")
class ChatImpl(
    private val chatServiceImpl: ChatService
) : ChatAPI {

    @LoggingRequest
    @LoggingResponse
    @PostMapping("/sendMessage")
    @ResponseBody
    override fun sendMessage(
        @RequestBody(required = true) request: MessageRequest
    ): MessageResponse {

        return APIWrapperException.executeRequest {
            val client = UUID.fromString(request.from)
            val coach = UUID.fromString(request.to)

            val message = MessageDto(
                message = request.message,
                to = if (request.owner == MessageRequest.Owner.CLIENT) client else coach,
                from = if (request.owner == MessageRequest.Owner.COACH) coach else client,
                owner = MessageDto.Owner.valueOf(request.owner.name)
            )

            val messageDto = chatService.sendMessage(message)

            MessageResponse(messageDto.identifier!!.toString())
        }
    }

    @LoggingRequest
    @LoggingResponse
    @GetMapping("/getAllMessages")
    @ResponseBody
    override fun getChat(
        @Valid request: ChatRequest
    ): ChatResponse {
        return APIWrapperException.executeRequest {

            val client = UUID.fromString(request.client)
            val coach = UUID.fromString(request.coach)

            val chatDto = chatService.getChat(client, coach)

            ChatResponse(
                chatDto.listOfMessages.map {
                    ChatResponse.Message(
                        content = it.message,
                        owner = it.owner.name
                    )
                }.toList()
            )
        }
    }
}
