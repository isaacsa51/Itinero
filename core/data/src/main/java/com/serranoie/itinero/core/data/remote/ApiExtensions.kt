package com.serranoie.itinero.core.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType

// Extension functions to make HTTP calls more declarative
suspend inline fun <reified T> HttpClient.getRequest(
    baseUrl: String,
    endpoint: String,
    queryParams: Map<String, Any> = emptyMap()
): T {
    return get("$baseUrl$endpoint") {
        queryParams.forEach { (key, value) ->
            parameter(key, value)
        }
    }.body()
}

suspend inline fun <reified T, reified R> HttpClient.postRequest(
    baseUrl: String,
    endpoint: String,
    body: R? = null,
    queryParams: Map<String, Any> = emptyMap()
): T {
    return post("$baseUrl$endpoint") {
        contentType(ContentType.Application.Json)
        queryParams.forEach { (key, value) ->
            parameter(key, value)
        }
        if (body != null) {
            setBody(body)
        }
    }.body()
}

suspend inline fun <reified T, reified R> HttpClient.putRequest(
    baseUrl: String,
    endpoint: String,
    body: R? = null
): T {
    return put("$baseUrl$endpoint") {
        contentType(ContentType.Application.Json)
        if (body != null) {
            setBody(body)
        }
    }.body()
}

suspend inline fun <reified T> HttpClient.deleteRequest(
    baseUrl: String,
    endpoint: String,
    pathParams: Map<String, Any> = emptyMap()
): T {
    var url = "$baseUrl$endpoint"
    pathParams.forEach { (key, value) ->
        url = url.replace("{$key}", value.toString())
    }
    return delete(url).body()
}