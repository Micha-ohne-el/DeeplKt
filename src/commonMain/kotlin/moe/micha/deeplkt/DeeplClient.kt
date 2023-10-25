package moe.micha.deeplkt

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.http.isSuccess
import io.ktor.http.parameters
import io.ktor.http.parametersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.StringValuesBuilder
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import moe.micha.deeplkt.document.DocumentResponse
import moe.micha.deeplkt.document.DocumentResponse.Done
import moe.micha.deeplkt.document.DocumentResponse.Error
import moe.micha.deeplkt.document.DocumentResponse.InProgress
import moe.micha.deeplkt.document.UploadDocumentResponse
import moe.micha.deeplkt.translate.Formality
import moe.micha.deeplkt.translate.OutlineDetection
import moe.micha.deeplkt.translate.PreserveFormatting
import moe.micha.deeplkt.translate.SplitSentences
import moe.micha.deeplkt.translate.TagHandling
import moe.micha.deeplkt.translate.TranslateResponse
import moe.micha.deeplkt.usage.Usage

class DeeplClient(
    private val authKey: String,
    httpClientEngine: HttpClientEngine = defaultHttpClientEngine,
    private val apiUrl: String = if (authKey.endsWith(":fx")) {
        "https://api-free.deepl.com/v2/"
    } else {
        "https://api.deepl.com/v2/"
    },
    extraHttpClientConfig: HttpClientConfig<*>.() -> Unit = {},
) {
    suspend fun translate(
        text: String,
        targetLang: TargetLang,
        sourceLang: SourceLang? = null,
        splitSentences: SplitSentences? = null,
        preserveFormatting: PreserveFormatting? = null,
        formality: Formality? = null,
        tagHandling: TagHandling? = null,
        nonSplittingTags: Iterable<String>? = null,
        outlineDetection: OutlineDetection? = null,
        splittingTags: Iterable<String>? = null,
        ignoreTags: Iterable<String>? = null,
    ) = translate(
        texts = arrayOf(text),
        targetLang,
        sourceLang,
        splitSentences,
        preserveFormatting,
        formality,
        tagHandling,
        nonSplittingTags,
        outlineDetection,
        splittingTags,
        ignoreTags,
    ).translations.first()

    suspend fun translate(
        vararg texts: String,
        targetLang: TargetLang,
        sourceLang: SourceLang? = null,
        splitSentences: SplitSentences? = null,
        preserveFormatting: PreserveFormatting? = null,
        formality: Formality? = null,
        tagHandling: TagHandling? = null,
        nonSplittingTags: Iterable<String>? = null,
        outlineDetection: OutlineDetection? = null,
        splittingTags: Iterable<String>? = null,
        ignoreTags: Iterable<String>? = null,
    ): TranslateResponse {
        val parameters = parameters {
            for (text in texts) {
                append("text", text)
            }
            append("target_lang", targetLang.code)
            append("source_lang", sourceLang?.code)
            append("split_sentences", splitSentences?.value)
            append("preserve_formatting", preserveFormatting?.value)
            append("formality", formality?.value)
            append("tag_handling", tagHandling?.value)
            append("non_splitting_tags", nonSplittingTags?.joinToString(","))
            append("outline_detection", outlineDetection?.value)
            append("splitting_tags", splittingTags?.joinToString(","))
            append("ignore_tags", ignoreTags?.joinToString(","))
        }

        val response = httpClient.submitForm("translate", parameters)

        return response.body()
    }

    suspend fun getUsage(): Usage = httpClient.get("usage").body()

    suspend fun translateDocument(
        content: String,
        targetLang: TargetLang,
        sourceLang: SourceLang? = null,
        fileName: String,
        formality: Formality? = null,
    ): String {
        val (id, key) = uploadDocument(content, targetLang, sourceLang, fileName, formality)

        awaitDocumentTranslation(id, key)

        return downloadDocumentTranslation(id, key)
    }


    private suspend fun uploadDocument(
        content: String,
        targetLang: TargetLang,
        sourceLang: SourceLang? = null,
        fileName: String,
        formality: Formality? = null,
    ): UploadDocumentResponse {
        return httpClient.submitFormWithBinaryData(
            url = "document",
            formData = formData {
                append("target_lang", targetLang.code)
                append("source_lang", sourceLang?.code)
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

    private val httpClient = HttpClient(httpClientEngine) {
        expectSuccess = true

        install(ContentNegotiation) {
            json()
        }

        defaultRequest {
            url(apiUrl)
            header("Authorization", "DeepL-Auth-Key $authKey")
        }

        install(HttpRequestRetry) {
            maxRetries = 3
            exponentialDelay()
            // resulting delays inbetween requests: 1s, 2s, 4s.

            retryIf { _, response ->
                !response.status.isSuccess() && response.status != HttpStatusCode(456, "Quota Exceeded")
            }
        }

        extraHttpClientConfig()
    }

    private fun StringValuesBuilder.append(name: String, value: String?) {
        if (value != null) {
            append(name, value)
        }
    }

    private fun FormBuilder.append(key: String, value: String?) {
        if (value != null) {
            append(key, value)
        }
    }
}
