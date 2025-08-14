package com.serranoie.app.feature.chat.data.remote.api

import com.serranoie.itinero.core.domain.exception.ChatApiException
import io.ktor.client.HttpClient
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatApiServiceTest {

    @Test(expected = ChatApiException::class)
    fun `getMessages throws on invalid group code`() = runTest {
        val service = ChatApiService(HttpClient(), baseUrl = "http://localhost")
        service.getMessages(groupCode = "invalid", authToken = "token", limit = 20, offset = 0)
    }
}
