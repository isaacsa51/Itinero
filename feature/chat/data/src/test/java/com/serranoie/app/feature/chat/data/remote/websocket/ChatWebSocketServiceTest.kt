package com.serranoie.app.feature.chat.data.remote.websocket

import com.serranoie.itinero.core.domain.exception.ChatApiException
import io.ktor.client.HttpClient
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertFailsWith

class ChatWebSocketServiceTest {

    @Test
    fun `sendTypingStart throws on invalid group code`() = runTest {
        val client = HttpClient()

        try {
            val service = ChatWebSocketService(client, baseUrl = "ws://example.invalid")

            assertFailsWith<ChatApiException> {
                service.sendTypingStart("BAD_CODE", authToken = "token")
            }
        } finally {
            client.close()
        }
    }
}
