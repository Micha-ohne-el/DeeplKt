package moe.micha.deeplkt

import moe.micha.deeplkt.translate.TranslateOptions
import moe.micha.deeplkt.translate.TranslateResponse
import moe.micha.deeplkt.translate.Translation

suspend fun DeeplClient.translate(
    text: String,
    to: TargetLang,
    from: SourceLang? = null,
    options: TranslateOptions = TranslateOptions(),
): Translation = translate(texts = arrayOf(text), to, from, options).translations.first()

suspend fun DeeplClient.translate(
    text: String,
    to: TargetLang,
    from: SourceLang? = null,
    buildOptions: TranslateOptions.() -> Unit = {},
): Translation = translate(text, to, from, TranslateOptions().apply(buildOptions))

suspend fun DeeplClient.translate(
    vararg texts: String,
    to: TargetLang,
    from: SourceLang? = null,
    buildOptions: TranslateOptions.() -> Unit = {},
): TranslateResponse = translate(texts = texts, to, from, TranslateOptions().apply(buildOptions))

suspend fun DeeplClient.translate(text: String, to: TargetLang, from: SourceLang): Translation =
    translate(text, to, from, options = TranslateOptions())

suspend fun DeeplClient.translate(vararg texts: String, to: TargetLang, from: SourceLang): TranslateResponse =
    translate(texts = texts, to, from, options = TranslateOptions())

suspend fun DeeplClient.translate(
    text: String,
    to: TargetLang,
    options: TranslateOptions = TranslateOptions(),
): Translation =
    translate(text, to, from = null, options)

suspend fun DeeplClient.translate(
    vararg texts: String,
    to: TargetLang,
    options: TranslateOptions = TranslateOptions(),
): TranslateResponse =
    translate(texts = texts, to, from = null, options)

suspend fun DeeplClient.translateText(
    text: String,
    to: TargetLang,
    from: SourceLang? = null,
    options: TranslateOptions = TranslateOptions(),
): String = translate(text, to, from, options).text

suspend fun DeeplClient.translateText(
    text: String,
    to: TargetLang,
    from: SourceLang? = null,
    buildOptions: TranslateOptions.() -> Unit = {},
): String = translate(text, to, from, buildOptions).text

suspend fun DeeplClient.translateText(
    vararg texts: String,
    to: TargetLang,
    from: SourceLang? = null,
    buildOptions: TranslateOptions.() -> Unit = {},
): List<String> = translate(texts = texts, to, from, buildOptions).translations.map(Translation::text)

suspend fun DeeplClient.translateText(text: String, to: TargetLang, from: SourceLang): String =
    translate(text, to, from).text

suspend fun DeeplClient.translateText(vararg texts: String, to: TargetLang, from: SourceLang): List<String> =
    translate(texts = texts, to, from).translations.map(Translation::text)

suspend fun DeeplClient.translateText(
    text: String,
    to: TargetLang,
    options: TranslateOptions = TranslateOptions(),
): String =
    translate(text, to, options).text

suspend fun DeeplClient.translateText(
    vararg texts: String,
    to: TargetLang,
    options: TranslateOptions = TranslateOptions(),
): List<String> =
    translate(texts = texts, to, options).translations.map(Translation::text)
