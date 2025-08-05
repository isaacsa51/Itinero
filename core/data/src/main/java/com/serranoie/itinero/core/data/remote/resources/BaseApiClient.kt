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
import com.serranoie.itinero.core.domain.exception.NetworkException
import com.serranoie.itinero.core.domain.exception.UnauthorizedException
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
        try {
            val response: HttpResponse = client.get("$baseUrl$endpoint") {
                queryParams.forEach { (key, value) ->
                    parameter(key, value)
                }
            }
            return handleResponse(response)
        } catch (e: Exception) {
            throw handleNetworkError(e)
        }
    }

    protected suspend inline fun <reified T, reified R> post(
        endpoint: String,
        body: R? = null,
        queryParams: Map<String, Any> = emptyMap()
    ): T {
        try {
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
        } catch (e: Exception) {
            throw handleNetworkError(e)
        }
    }

    protected suspend inline fun <reified T, reified R> put(
        endpoint: String,
        body: R? = null
    ): T {
        try {
            val response: HttpResponse = client.put("$baseUrl$endpoint") {
                contentType(ContentType.Application.Json)
                if (body != null) {
                    setBody(body)
                }
            }
            return handleResponse(response)
        } catch (e: Exception) {
            throw handleNetworkError(e)
        }
    }

    protected suspend inline fun <reified T> delete(
        endpoint: String
    ): T {
        try {
            val response: HttpResponse = client.delete("$baseUrl$endpoint")
            return handleResponse(response)
        } catch (e: Exception) {
            throw handleNetworkError(e)
        }
    }

    protected suspend inline fun <reified T, reified R> deleteWithBody(
        endpoint: String,
        body: R
    ): T {
        try {
            val response: HttpResponse = client.delete("$baseUrl$endpoint") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            return handleResponse(response)
        } catch (e: Exception) {
            throw handleNetworkError(e)
        }
    }

    protected suspend inline fun <reified T> patch(
        endpoint: String,
        body: Any? = null
    ): T {
        try {
            val response: HttpResponse = client.patch("$baseUrl$endpoint") {
                contentType(ContentType.Application.Json)
                if (body != null) {
                    setBody(body)
                }
            }
            return handleResponse(response)
        } catch (e: Exception) {
            throw handleNetworkError(e)
        }
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

            // Handle 404 User not found specifically
            if (response.status == HttpStatusCode.NotFound &&
                errorBody.contains("User not found", ignoreCase = true)
            ) {
                Log.e("ITINERO - API ERROR", "404 User not found - triggering automatic logout")
                UnauthorizedHandler.handleUnauthorized()
                throw UnauthorizedException("User not found. Please log in again.")
            }

            throw Exception("API Error: ${response.status.value} - $errorBody")
        }

        return response.body()
    }

    protected fun handleNetworkError(exception: Throwable): Exception {
        return when {
            exception.message?.contains("Connect timeout", ignoreCase = true) == true -> {
                NetworkException("Unable to connect to server. Please check your internet connection and try again.")
            }

            exception.message?.contains("timeout", ignoreCase = true) == true -> {
                NetworkException("Request timed out. Please check your internet connection and try again.")
            }

            exception.message?.contains("No route to host", ignoreCase = true) == true -> {
                NetworkException("Server is unreachable. Please check your internet connection.")
            }

            exception.message?.contains("Connection refused", ignoreCase = true) == true -> {
                NetworkException("Unable to connect to server. The service may be temporarily unavailable.")
            }

            else -> {
                NetworkException("Network error occurred. Please check your internet connection and try again.")
            }
        }
    }
}
