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
    to: TargetLang,
    from: SourceLang? = null,
    context: String,
    options: TranslateOptions = TranslateOptions(),
): TranslateResponse {
    val parameters = parameters {
        appendAll("text", texts.asIterable())
        append("target_lang", to.code)
        append("source_lang", from?.code)
        append("context", context)
        appendAll(options.toParameters())
    }

    val response = httpClient.submitForm("translate", parameters)

    return response.body()
}

@DeeplAlphaApi
suspend fun DeeplClient.translate(
    text: String,
    to: TargetLang,
    from: SourceLang? = null,
    context: String,
    options: TranslateOptions = TranslateOptions(),
): Translation = translate(texts = arrayOf(text), to, from, context, options).translations.first()

@DeeplAlphaApi
suspend fun DeeplClient.translate(
    text: String,
    to: TargetLang,
    from: SourceLang? = null,
    context: String,
    buildOptions: TranslateOptions.() -> Unit = {},
): Translation = translate(text, to, from, context, TranslateOptions().apply(buildOptions))

@DeeplAlphaApi
suspend fun DeeplClient.translate(
    vararg texts: String,
    to: TargetLang,
    from: SourceLang? = null,
    context: String,
    buildOptions: TranslateOptions.() -> Unit = {},
): TranslateResponse = translate(texts = texts, to, from, context, TranslateOptions().apply(buildOptions))

@DeeplAlphaApi
suspend fun DeeplClient.translate(text: String, to: TargetLang, from: SourceLang, context: String): Translation =
    translate(text, to, from, context, options = TranslateOptions())

@DeeplAlphaApi
suspend fun DeeplClient.translate(
    vararg texts: String,
    to: TargetLang,
    from: SourceLang,
    context: String,
): TranslateResponse =
    translate(texts = texts, to, from, context, options = TranslateOptions())

@DeeplAlphaApi
suspend fun DeeplClient.translate(
    text: String,
    to: TargetLang,
    context: String,
    options: TranslateOptions = TranslateOptions(),
): Translation =
    translate(text, to, from = null, context, options)

@DeeplAlphaApi
suspend fun DeeplClient.translate(
    vararg texts: String,
    to: TargetLang,
    context: String,
    options: TranslateOptions = TranslateOptions(),
): TranslateResponse =
    translate(texts = texts, to, from = null, context, options)

@DeeplAlphaApi
suspend fun DeeplClient.translateText(
    vararg texts: String,
    to: TargetLang,
    from: SourceLang? = null,
    context: String,
    options: TranslateOptions = TranslateOptions(),
): List<String> = translate(texts = texts, to, from, context, options).translations.map(Translation::text)

@DeeplAlphaApi
suspend fun DeeplClient.translateText(
    text: String,
    to: TargetLang,
    from: SourceLang? = null,
    context: String,
    options: TranslateOptions = TranslateOptions(),
): String = translate(text, to, from, context, options).text

@DeeplAlphaApi
suspend fun DeeplClient.translateText(
    text: String,
    to: TargetLang,
    from: SourceLang? = null,
    context: String,
    buildOptions: TranslateOptions.() -> Unit = {},
): String = translate(text, to, from, context, buildOptions).text

@DeeplAlphaApi
suspend fun DeeplClient.translateText(
    vararg texts: String,
    to: TargetLang,
    from: SourceLang? = null,
    context: String,
    buildOptions: TranslateOptions.() -> Unit = {},
): List<String> = translate(texts = texts, to, from, context, buildOptions).translations.map(Translation::text)

@DeeplAlphaApi
suspend fun DeeplClient.translateText(text: String, to: TargetLang, from: SourceLang, context: String): String =
    translate(text, to, from, context).text

@DeeplAlphaApi
suspend fun DeeplClient.translateText(
    vararg texts: String,
    to: TargetLang,
    from: SourceLang,
    context: String,
): List<String> =
    translate(texts = texts, to, from, context).translations.map(Translation::text)

@DeeplAlphaApi
suspend fun DeeplClient.translateText(
    text: String,
    to: TargetLang,
    context: String,
    options: TranslateOptions = TranslateOptions(),
): String =
    translate(text, to, context, options).text

@DeeplAlphaApi
suspend fun DeeplClient.translateText(
    vararg texts: String,
    to: TargetLang,
    context: String,
    options: TranslateOptions = TranslateOptions(),
): List<String> =
    translate(texts = texts, to, context, options).translations.map(Translation::text)
