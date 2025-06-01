package com.eventify.service

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.*

object WablasService {
    private const val token = "gBFe8yBpYulaFl25OOZ6CeC9ynt29VngPW6iVaN6UfLGLll3ghOV4i0"
    private const val url = "https://texas.wablas.com/api/v2/send-message"

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }

    suspend fun sendMessage(phone: String, message: String): Boolean {
        return try {
            val response: HttpResponse = client.post(url) {
                headers {
                    append("Authorization", token)
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                setBody(
                    SendMessageArrayRequest(
                        data = listOf(
                            Message(phone = phone, message = message)
                        )
                    )
                )
            }

            val responseBody = response.bodyAsText()
            println("Wablas response status: ${response.status}")
            println("Wablas response body: $responseBody")

            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            println("Error saat mengirim pesan ke Wablas: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    @Serializable
    data class SendMessageArrayRequest(
        val data: List<Message>
    )

    @Serializable
    data class Message(
        val phone: String,
        val message: String,
        val secret: Boolean = false,
        val priority: Boolean = false
    )
}
