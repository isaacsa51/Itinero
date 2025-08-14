package com.serranoie.app.feature.chat.data.remote.api

import com.serranoie.itinero.core.domain.exception.ChatApiException
import io.ktor.client.HttpClient
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatApiServiceTest {

    @Test(expected = ChatApiException::class)
    fun `getMessages throws on invalid group code`() = runBlockingUnit {
        val service = ChatApiService(HttpClient(), baseUrl = "http://localhost")
        service.getMessages(groupCode = "invalid", authToken = "token", limit = 20, offset = 0)
    }
}

fun runBlockingUnit(block: suspend () -> Unit) {
    kotlinx.coroutines.runBlocking { block() }
}
