package com.coach.flame.api.networking.response

data class ChatResponse(
    val messages: List<Message>
) {
    data class Message(
        val content: String,
        val owner: String
    )
}
