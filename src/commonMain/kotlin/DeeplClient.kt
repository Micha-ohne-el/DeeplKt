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
    ) {
        httpClient.submitForm("translate") {
            parameter("text", text)
            parameter("target_lang", targetLang.code)
            parameter("source_lang", sourceLang?.code)
            parameter("split_sentences", splitSentences?.value)
            parameter("preserve_formatting", preserveFormatting?.value)
            parameter("formality", formality?.value)
            parameter("tag_handling", tagHandling?.value)
            parameter("non_splitting_tags", nonSplittingTags?.joinToString(","))
            parameter("outline_detection", outlineDetection?.value)
            parameter("splitting_tags", splittingTags?.joinToString(","))
            parameter("ignore_tags", ignoreTags?.joinToString(","))
        }
    }
}
