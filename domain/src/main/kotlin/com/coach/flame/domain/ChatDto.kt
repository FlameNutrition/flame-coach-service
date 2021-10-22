package com.coach.flame.domain

data class ChatDto(
    val listOfMessages: MutableList<MessageDto>,
    val client: ClientDto,
    val coach: CoachDto
) {
    override fun toString(): String {
        return "ChatDto(" +
                "listOfMessages=$listOfMessages, " +
                "client=$client, " +
                "coach=$coach" +
                ")"
    }
}
