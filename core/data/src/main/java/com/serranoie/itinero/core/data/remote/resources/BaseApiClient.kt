/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: BaseApiClient.kt
 - Project: Itinero
 - Module: Itinero.core.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 22 junio 2025
 */

package com.serranoie.itinero.core.data.remote.resources

import android.util.Log
import com.serranoie.itinero.core.data.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess

abstract class BaseApiClient(
    protected val client: HttpClient,
    protected val baseUrl: String = BuildConfig.API_BASE_URL
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

            // Handle 401 Unauthorized specifically
            if (response.status == HttpStatusCode.Unauthorized) {
                Log.e("ITINERO - API ERROR", "401 Unauthorized - triggering automatic logout")
                UnauthorizedHandler.handleUnauthorized()
                throw UnauthorizedException("Session expired. Please log in again.")
            }

            throw Exception("API Error: ${response.status.value} - $errorBody")
        }

        return response.body()
    }
}

/**
 * Exception thrown when a 401 Unauthorized response is received
 */
class UnauthorizedException(message: String) : Exception(message)
