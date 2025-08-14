package com.serranoie.app.feature.chat.data.remote.websocket

import com.serranoie.itinero.core.domain.exception.ChatApiException
import io.ktor.client.HttpClient
import org.junit.Test

class ChatWebSocketServiceTest {

    @Test(expected = ChatApiException::class)
    fun `sendTypingStart throws on invalid group code`() = runBlockingUnit {
        val service = ChatWebSocketService(HttpClient(), baseUrl = "ws://localhost")
        service.sendTypingStart(groupCode = "BAD_CODE", authToken = "token")
    }
}

private fun runBlockingUnit(block: suspend () -> Unit) {
    kotlinx.coroutines.runBlocking { block() }
}
