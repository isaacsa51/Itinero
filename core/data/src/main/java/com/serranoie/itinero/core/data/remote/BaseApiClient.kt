package com.serranoie.itinero.core.data.remote

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess

abstract class BaseApiClient(
    protected val client: HttpClient,
    protected val baseUrl: String = "http://192.168.100.3:8080"
) {

    /**
     * Sends an HTTP GET request to the specified endpoint with optional query parameters and returns the response deserialized to the specified type.
     *
     * @param endpoint The API endpoint to send the GET request to, relative to the base URL.
     * @param queryParams Optional query parameters to include in the request.
     * @return The response body deserialized as the specified type [T].
     */
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

    /**
     * Sends an HTTP POST request to the specified endpoint with an optional JSON body and query parameters, and returns the deserialized response.
     *
     * @param endpoint The API endpoint to send the POST request to.
     * @param body The optional request body to be serialized as JSON.
     * @param queryParams Optional query parameters to include in the request URL.
     * @return The response body deserialized to type [T].
     */
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

    /**
     * Sends an HTTP PUT request to the specified endpoint with an optional JSON body and returns the deserialized response.
     *
     * @param endpoint The API endpoint to send the PUT request to, relative to the base URL.
     * @param body The optional request body to be serialized as JSON.
     * @return The response body deserialized to type [T].
     */
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

    /**
     * Sends an HTTP DELETE request to the specified endpoint and returns the response deserialized as type [T].
     *
     * @param endpoint The API endpoint to send the DELETE request to, relative to the base URL.
     * @return The response body deserialized as type [T].
     */
    protected suspend inline fun <reified T> delete(
        endpoint: String
    ): T {
        val response: HttpResponse = client.delete("$baseUrl$endpoint")
        return handleResponse(response)
    }

    /**
     * Sends an HTTP PATCH request with an optional JSON body to the specified endpoint and returns the deserialized response.
     *
     * @param endpoint The API endpoint to send the PATCH request to.
     * @param body The optional request body to be serialized as JSON.
     * @return The response body deserialized to the specified type [T].
     */
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

    /**
     * Processes an HTTP response, throwing exceptions for error statuses and returning the deserialized body for successful responses.
     *
     * If the response status is 401 Unauthorized, triggers automatic logout and throws an [UnauthorizedException].
     * For other error statuses, throws a generic [Exception] with the status code and error message.
     *
     * @param response The HTTP response to process.
     * @return The response body deserialized to the expected type [T].
     */
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
