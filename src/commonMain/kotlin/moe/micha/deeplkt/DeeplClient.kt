package moe.micha.deeplkt

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.http.parameters
import io.ktor.http.parametersOf
import io.ktor.serialization.kotlinx.json.json
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import moe.micha.deeplkt.document.DocumentResponse
import moe.micha.deeplkt.document.DocumentResponse.Done
import moe.micha.deeplkt.document.DocumentResponse.Error
import moe.micha.deeplkt.document.DocumentResponse.InProgress
import moe.micha.deeplkt.document.UploadDocumentResponse
import moe.micha.deeplkt.internal.QuotaExceeded
import moe.micha.deeplkt.internal.append
import moe.micha.deeplkt.internal.defaultHttpClientEngine
import moe.micha.deeplkt.retrying.FailureReason.OtherError
import moe.micha.deeplkt.retrying.FailureReason.QuotaExceeded
import moe.micha.deeplkt.retrying.FailureReason.ServerError
import moe.micha.deeplkt.retrying.FailureReason.TooManyRequests
import moe.micha.deeplkt.retrying.RetryConfig
import moe.micha.deeplkt.translate.Formality
import moe.micha.deeplkt.translate.TranslateOptions
import moe.micha.deeplkt.translate.TranslateResponse
import moe.micha.deeplkt.translate.Translation
import moe.micha.deeplkt.usage.Usage

class DeeplClient(
    private val authKey: String,
    httpClientEngine: HttpClientEngine = defaultHttpClientEngine,
    apiUrl: String? = null,
    retryConfig: (RetryConfig.() -> Unit)? = null,
) {
    suspend fun translate(
        vararg texts: String,
        to: TargetLang,
        from: SourceLang? = null,
        options: TranslateOptions = TranslateOptions(),
    ): TranslateResponse {
        val parameters = parameters {
            appendAll("text", texts.asIterable())
            append("target_lang", to.code)
            append("source_lang", from?.code)
            appendAll(options.toParameters())
        }

        val response = httpClient.submitForm("translate", parameters)

        return response.body()
    }

    suspend fun translateText(
        vararg texts: String,
        to: TargetLang,
        from: SourceLang? = null,
        options: TranslateOptions = TranslateOptions(),
    ): List<String> = translate(texts = texts, to, from, options).translations.map(Translation::text)

    suspend fun getUsage(): Usage = httpClient.get("usage").body()

    suspend fun translateDocument(
        content: String,
        fileName: String,
        to: TargetLang,
        from: SourceLang? = null,
        formality: Formality? = null,
    ): String {
        val (id, key) = uploadDocument(content, fileName, to, from, formality)

        awaitDocumentTranslation(id, key)

        return downloadDocumentTranslation(id, key)
    }


    private val apiUrl = apiUrl ?: if (authKey.endsWith(":fx")) {
        "https://api-free.deepl.com/v2/"
    } else {
        "https://api.deepl.com/v2/"
    }

    private val shouldRetry: RetryConfig.() -> Unit = retryConfig ?: {
        when (failureReason) {
            QuotaExceeded -> stopRequest()
            else -> retry()
        }
    }

    private suspend fun uploadDocument(
        content: String,
        fileName: String,
        to: TargetLang,
        from: SourceLang? = null,
        formality: Formality? = null,
    ): UploadDocumentResponse {
        return httpClient.submitFormWithBinaryData(
            url = "document",
            formData = formData {
                append("target_lang", to.code)
                append("source_lang", from?.code)
                append("formality", formality?.value)
                append("file", content, headersOf(HttpHeaders.ContentDisposition, """filename="$fileName""""))
            }
        ).body()
    }

    private suspend fun awaitDocumentTranslation(id: String, key: String): Done {
        for (n in 0..Int.MAX_VALUE) {
            val response: DocumentResponse = httpClient.submitForm("document/$id", parametersOf("document_key", key)).body()

            if (response is InProgress) {
                val delayDuration = if (response.timeRemaining != null) response.timeRemaining!! * 0.1 else 1.seconds
                if (n >= 5) delay(delayDuration)
            } else if (response is Error) {
                throw RuntimeException(response.message)
            } else if (response is Done) {
                return response
            }
        }

        throw RuntimeException("Document translation took forever.")
    }

    private suspend fun downloadDocumentTranslation(id: String, key: String): String {
        return httpClient.submitForm("document/$id/result", parametersOf("document_key", key)).bodyAsText()
    }

    internal val httpClient = HttpClient(httpClientEngine) {
        expectSuccess = true

        install(ContentNegotiation) {
            json()
        }

        defaultRequest {
            url(this@DeeplClient.apiUrl)
            header("Authorization", "DeepL-Auth-Key $authKey")
        }

        install(HttpRequestRetry) {
            retryIf { _, response ->
                if (response.status.value < 400) {
                    return@retryIf false
                }

                val failureReason = when {
                    response.status == HttpStatusCode.TooManyRequests -> TooManyRequests
                    response.status == HttpStatusCode.QuotaExceeded -> QuotaExceeded
                    response.status.value in 500..599 -> ServerError

                    else -> OtherError
                }

                val config = RetryConfig(retryCount, failureReason)
                config.shouldRetry()

                maxRetries = config.retryCap ?: Int.MAX_VALUE
                exponentialDelay(maxDelayMs = config.delayCap?.inWholeMilliseconds ?: Long.MAX_VALUE)

                config.shouldRetry
            }
        }
    }
}
