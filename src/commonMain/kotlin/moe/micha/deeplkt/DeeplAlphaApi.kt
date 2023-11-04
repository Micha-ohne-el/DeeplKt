package moe.micha.deeplkt

import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.http.parameters
import kotlin.RequiresOptIn.Level.ERROR
import moe.micha.deeplkt.internal.append
import moe.micha.deeplkt.translate.TranslateOptions
import moe.micha.deeplkt.translate.TranslateResponse
import moe.micha.deeplkt.translate.Translation

@RequiresOptIn(
    "DeepL alpha features may break or be removed at any moment! Only use them for testing purposes!",
    level = ERROR,
)
annotation class DeeplAlphaApi

@DeeplAlphaApi
suspend fun DeeplClient.translate(
    vararg texts: String,
    targetLang: TargetLang,
    sourceLang: SourceLang? = null,
    context: String,
    options: TranslateOptions = TranslateOptions(),
): TranslateResponse {
    val parameters = parameters {
        appendAll("text", texts.asIterable())
        append("target_lang", targetLang.code)
        append("source_lang", sourceLang?.code)
        append("context", context)
        appendAll(options.toParameters())
    }

    val response = httpClient.submitForm("translate", parameters)

    return response.body()
}

@DeeplAlphaApi
suspend fun DeeplClient.translate(
    text: String,
    targetLang: TargetLang,
    sourceLang: SourceLang? = null,
    context: String,
    options: TranslateOptions = TranslateOptions(),
): Translation = translate(texts = arrayOf(text), targetLang, sourceLang, context, options).translations.first()

@DeeplAlphaApi
suspend fun DeeplClient.translate(
    text: String,
    targetLang: TargetLang,
    sourceLang: SourceLang? = null,
    context: String,
    buildOptions: TranslateOptions.() -> Unit = {},
): Translation = translate(text, targetLang, sourceLang, context, TranslateOptions().apply(buildOptions))

@DeeplAlphaApi
suspend fun DeeplClient.translate(
    vararg texts: String,
    targetLang: TargetLang,
    sourceLang: SourceLang? = null,
    context: String,
    buildOptions: TranslateOptions.() -> Unit = {},
): TranslateResponse = translate(texts = texts, targetLang, sourceLang, context, TranslateOptions().apply(buildOptions))

@DeeplAlphaApi
suspend fun DeeplClient.translate(text: String, targetLang: TargetLang, sourceLang: SourceLang, context: String): Translation =
    translate(text, targetLang, sourceLang, context, options = TranslateOptions())

@DeeplAlphaApi
suspend fun DeeplClient.translate(
    vararg texts: String,
    targetLang: TargetLang,
    sourceLang: SourceLang,
    context: String,
): TranslateResponse =
    translate(texts = texts, targetLang, sourceLang, context, options = TranslateOptions())

@DeeplAlphaApi
suspend fun DeeplClient.translate(
    text: String,
    targetLang: TargetLang,
    context: String,
    options: TranslateOptions = TranslateOptions(),
): Translation =
    translate(text, targetLang, sourceLang = null, context, options)

@DeeplAlphaApi
suspend fun DeeplClient.translate(
    vararg texts: String,
    targetLang: TargetLang,
    context: String,
    options: TranslateOptions = TranslateOptions(),
): TranslateResponse =
    translate(texts = texts, targetLang, sourceLang = null, context, options)

@DeeplAlphaApi
suspend fun DeeplClient.translateText(
    vararg texts: String,
    targetLang: TargetLang,
    sourceLang: SourceLang? = null,
    context: String,
    options: TranslateOptions = TranslateOptions(),
): List<String> = translate(texts = texts, targetLang, sourceLang, context, options).translations.map(Translation::text)

@DeeplAlphaApi
suspend fun DeeplClient.translateText(
    text: String,
    targetLang: TargetLang,
    sourceLang: SourceLang? = null,
    context: String,
    options: TranslateOptions = TranslateOptions(),
): String = translate(text, targetLang, sourceLang, context, options).text

@DeeplAlphaApi
suspend fun DeeplClient.translateText(
    text: String,
    targetLang: TargetLang,
    sourceLang: SourceLang? = null,
    context: String,
    buildOptions: TranslateOptions.() -> Unit = {},
): String = translate(text, targetLang, sourceLang, context, buildOptions).text

@DeeplAlphaApi
suspend fun DeeplClient.translateText(
    vararg texts: String,
    targetLang: TargetLang,
    sourceLang: SourceLang? = null,
    context: String,
    buildOptions: TranslateOptions.() -> Unit = {},
): List<String> = translate(texts = texts, targetLang, sourceLang, context, buildOptions).translations.map(Translation::text)

@DeeplAlphaApi
suspend fun DeeplClient.translateText(text: String, targetLang: TargetLang, sourceLang: SourceLang, context: String): String =
    translate(text, targetLang, sourceLang, context).text

@DeeplAlphaApi
suspend fun DeeplClient.translateText(
    vararg texts: String,
    targetLang: TargetLang,
    sourceLang: SourceLang,
    context: String,
): List<String> =
    translate(texts = texts, targetLang, sourceLang, context).translations.map(Translation::text)

@DeeplAlphaApi
suspend fun DeeplClient.translateText(
    text: String,
    targetLang: TargetLang,
    context: String,
    options: TranslateOptions = TranslateOptions(),
): String =
    translate(text, targetLang, context, options).text

@DeeplAlphaApi
suspend fun DeeplClient.translateText(
    vararg texts: String,
    targetLang: TargetLang,
    context: String,
    options: TranslateOptions = TranslateOptions(),
): List<String> =
    translate(texts = texts, targetLang, context, options).translations.map(Translation::text)
