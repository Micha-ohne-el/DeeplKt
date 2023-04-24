package moe.micha.deeplkt

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.parameter

class DeeplClient(
    private val authKey: String,
    httpClientEngine: HttpClientEngine = defaultHttpClientEngine,
    private val apiUrl: String = if (authKey.endsWith(":fx")) {
        "https://api-free.deepl.com/v2/"
    } else {
        "https://api.deepl.com/v2/"
    },
) {
    private val httpClient = HttpClient(httpClientEngine) {
        install(ContentNegotiation)

        defaultRequest {
            url(apiUrl)
        }
    }

    suspend fun translate(text: String, targetLang: String) {
        httpClient.submitForm("v2/translate") {
            parameter("text", text)
            parameter("targetLang", targetLang)
        }
    }
}
