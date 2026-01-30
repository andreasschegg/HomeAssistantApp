package com.example.homeassistantapp.data.api

import com.example.homeassistantapp.data.model.HomeAssistantState
import com.example.homeassistantapp.data.model.ServiceCall
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class HomeAssistantApi(
    private val baseUrl: String,
    private val accessToken: String
) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }

    suspend fun getStates(): Result<List<HomeAssistantState>> {
        return try {
            val response = client.get("$baseUrl/api/states") {
                header("Authorization", "Bearer $accessToken")
                contentType(ContentType.Application.Json)
            }
            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getState(entityId: String): Result<HomeAssistantState> {
        return try {
            val response = client.get("$baseUrl/api/states/$entityId") {
                header("Authorization", "Bearer $accessToken")
                contentType(ContentType.Application.Json)
            }
            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun callService(domain: String, service: String, entityId: String): Result<Unit> {
        return try {
            client.post("$baseUrl/api/services/$domain/$service") {
                header("Authorization", "Bearer $accessToken")
                contentType(ContentType.Application.Json)
                setBody(mapOf("entity_id" to entityId))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun turnOn(entityId: String): Result<Unit> {
        val domain = entityId.substringBefore(".")
        return callService(domain, "turn_on", entityId)
    }

    suspend fun turnOff(entityId: String): Result<Unit> {
        val domain = entityId.substringBefore(".")
        return callService(domain, "turn_off", entityId)
    }

    suspend fun toggle(entityId: String): Result<Unit> {
        val domain = entityId.substringBefore(".")
        return callService(domain, "toggle", entityId)
    }

    fun close() {
        client.close()
    }
}
