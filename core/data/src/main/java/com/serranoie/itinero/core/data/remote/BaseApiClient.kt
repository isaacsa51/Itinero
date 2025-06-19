package com.serranoie.itinero.core.data.remote

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

abstract class BaseApiClient(
    protected val client: HttpClient,
    protected val baseUrl: String = "http://192.168.100.3:8080"
) {

    protected suspend inline fun <reified T> get(
        endpoint: String,
        queryParams: Map<String, Any> = emptyMap()
    ): T {
        val response: HttpResponse = client.get("$baseUrl$endpoint") {
            queryParams.forEach { (key, value) ->
                parameter(key, value)
            }
        }
        return handleResponse(response)
    }

    protected suspend inline fun <reified T, reified R> post(
        endpoint: String,
        body: R? = null,
        queryParams: Map<String, Any> = emptyMap()
    ): T {
        val response: HttpResponse = client.post("$baseUrl$endpoint") {
            contentType(ContentType.Application.Json)
            queryParams.forEach { (key, value) ->
                parameter(key, value)
            }
            if (body != null) {
                setBody(body)
            }
        }
        return handleResponse(response)
    }

    protected suspend inline fun <reified T, reified R> put(
        endpoint: String,
        body: R? = null
    ): T {
        val response: HttpResponse = client.put("$baseUrl$endpoint") {
            contentType(ContentType.Application.Json)
            if (body != null) {
                setBody(body)
            }
        }
        return handleResponse(response)
    }

    protected suspend inline fun <reified T> delete(
        endpoint: String
    ): T {
        val response: HttpResponse = client.delete("$baseUrl$endpoint")
        return handleResponse(response)
    }

    protected suspend inline fun <reified T> patch(
        endpoint: String,
        body: Any? = null
    ): T {
        val response: HttpResponse = client.patch("$baseUrl$endpoint") {
            contentType(ContentType.Application.Json)
            if (body != null) {
                setBody(body)
            }
        }
        return handleResponse(response)
    }

    protected suspend inline fun <reified T> handleResponse(response: HttpResponse): T {
        if (!response.status.isSuccess()) {
            val errorBody = try {
                response.body<String>()
            } catch (e: Exception) {
                "Unknown error: $e"
            }

            Log.e("ITINERO - API ERROR", "Error in API call: $errorBody")
            Log.e("ITINERO - API ERROR", "In response from `${response.request.url}`")
            Log.e("ITINERO - API ERROR", "Response status `${response.status}`")
            Log.e("ITINERO - API ERROR", "Response header `ContentType: ${response.contentType()}`")

            throw Exception("API Error: ${response.status.value} - $errorBody")
        }

        return response.body()
    }
}
