package com.serranoie.itinero.di

import com.serranoie.itinero.core.data.network.NetworkConnectivityManager
import com.serranoie.itinero.core.domain.repository.AuthPreferencesRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.pingInterval
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import kotlin.time.Duration.Companion.seconds

val networkModule = module {
    single { NetworkConnectivityManager(androidContext()) }

    factory {
        val authPreferencesRepository = get<AuthPreferencesRepository>()

        HttpClient(OkHttp) {

            expectSuccess = false

            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    explicitNulls = false
                    coerceInputValues = true
                })
            }

            install(WebSockets) {
                pingInterval = 20.seconds
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 30_000L // 30 seconds
                connectTimeoutMillis = 15_000L // 15 seconds
                socketTimeoutMillis = 30_000L // 30 seconds
            }

            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.INFO
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val token = authPreferencesRepository.getToken()
                        if (!token.isNullOrBlank()) {
                            BearerTokens(accessToken = token, refreshToken = null)
                        } else {
                            null
                        }
                    }
                }
            }

            install(DefaultRequest) {
                header("Accept", "application/json")
                contentType(ContentType.Application.Json)
            }
        }
    }
}
