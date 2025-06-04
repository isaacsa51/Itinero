package com.serranoie.itinero.core.data.remote

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

suspend inline fun <reified T> HttpClient.postRequest(
    baseUrl: String,
    endpoint: String,
    body: Any? = null
): T {
    val response: HttpResponse = post("$baseUrl$endpoint") {
        contentType(ContentType.Application.Json)
        if (body != null) {
            setBody(body)
        }
    }

    if (!response.status.isSuccess()) {
        val errorBody = try {
            response.body<String>()
        } catch (e: Exception) {
            "Unknown error"
        }
        Log.e("API_ERROR", "Error in API call: $errorBody")
        Log.e("API_ERROR", "In response from `${response.request.url}`")
        Log.e("API_ERROR", "Response status `${response.status}` ")
        Log.e("API_ERROR", "Response header `ContentType: ${response.contentType()}` ")
        Log.e("API_ERROR", "Request header `Accept: application/json`")

        throw Exception("API Error: ${response.status.value} - $errorBody")
    }

    return response.body()
}

suspend inline fun <reified T> HttpClient.getRequest(
    baseUrl: String,
    endpoint: String
): T {
    val response: HttpResponse = get("$baseUrl$endpoint")

    if (!response.status.isSuccess()) {
        val errorBody = try {
            response.body<String>()
        } catch (e: Exception) {
            "Unknown error"
        }
        Log.e("API_ERROR", "Error in API call: $errorBody")
        throw Exception("API Error: ${response.status.value} - $errorBody")
    }

    return response.body()
}