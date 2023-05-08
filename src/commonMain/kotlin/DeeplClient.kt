package moe.micha.deeplkt

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.forms.submitForm
import io.ktor.http.ParametersBuilder
import io.ktor.http.parameters

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
    )

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
    ) {
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

        httpClient.submitForm("translate", parameters)
    }


    private fun ParametersBuilder.append(name: String, value: String?) {
        if (value != null) {
            append(name, value)
        }
    }
}
