package com.coach.flame.api.networking

import com.coach.flame.api.networking.request.ChatRequest
import com.coach.flame.api.networking.request.MessageRequest
import com.coach.flame.domain.ChatDto
import com.coach.flame.domain.MessageDto
import com.coach.flame.socialnetworking.ChatService
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockKExtension::class)
class ChatImplTest {

    @MockK
    private lateinit var chatServiceImpl: ChatService

    @InjectMockKs
    private lateinit var classToTest: ChatImpl

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `test send new message`() {

        val request = MessageRequest(
            "e69108e1-05ae-4205-bdce-01403b058149",
            "9521210b-70da-4ba1-8de1-403db5afa699",
            "This is the message",
            MessageRequest.Owner.CLIENT
        )

        val messageSlot = slot<MessageDto>()
        val messageDto = mockk<MessageDto>()

        every { messageDto.identifier } returns UUID.randomUUID()
        every { chatServiceImpl.sendMessage(capture(messageSlot)) } returns messageDto

        val response = classToTest.sendMessage(request)

        verify(exactly = 1) { chatServiceImpl.sendMessage(any()) }
        then(response).isNotNull
        then(response.identifier).isNotEmpty

        then(messageSlot.isCaptured).isTrue
        then(messageSlot.captured.identifier).isNotNull
        then(messageSlot.captured.time).isNotNull
        then(messageSlot.captured.from).isEqualTo(UUID.fromString("e69108e1-05ae-4205-bdce-01403b058149"))
        then(messageSlot.captured.to).isEqualTo(UUID.fromString("9521210b-70da-4ba1-8de1-403db5afa699"))
        then(messageSlot.captured.owner).isEqualTo(MessageDto.Owner.CLIENT)
    }

    @Test
    fun `test get all messages - chat`() {

        val request = ChatRequest().apply {
            client = "e69108e1-05ae-4205-bdce-01403b058149"
            coach = "9521210b-70da-4ba1-8de1-403db5afa699"
        }

        val message = mockk<MessageDto>()

        every { message.message } returns "Hello"
        every { message.owner } returns MessageDto.Owner.CLIENT
        every { message.time } returns LocalDateTime.now()
        every { chatServiceImpl.getChat(any(), any()) } returns ChatDto(
            listOfMessages = mutableListOf(message, message),
            client = mockk(),
            coach = mockk()
        )

        val response = classToTest.getChat(request)

        verify(exactly = 1) {
            chatServiceImpl.getChat(
                UUID.fromString("e69108e1-05ae-4205-bdce-01403b058149"),
                UUID.fromString("9521210b-70da-4ba1-8de1-403db5afa699")
            )
        }
        then(response).isNotNull
        then(response.messages).hasSize(2)
        then(response.messages.first().content).isEqualTo("Hello")
        then(response.messages.first().owner).isEqualTo("CLIENT")

    }

}