package com.serranoie.itinero.di

import com.serranoie.itinero.core.data.local.persistence.AuthPreferences
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val networkModule = module {
    single {
        AuthPreferences(androidContext())
    }

    factory {
        val authPreferences = get<AuthPreferences>()

        HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            
            install(Logging) {
                level = LogLevel.ALL
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val token = authPreferences.getToken()
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
