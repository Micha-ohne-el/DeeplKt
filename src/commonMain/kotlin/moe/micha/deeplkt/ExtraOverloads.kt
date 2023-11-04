package moe.micha.deeplkt

import moe.micha.deeplkt.translate.TranslateOptions
import moe.micha.deeplkt.translate.TranslateResponse
import moe.micha.deeplkt.translate.Translation

suspend fun DeeplClient.translate(
    text: String,
    targetLang: TargetLang,
    sourceLang: SourceLang? = null,
    options: TranslateOptions = TranslateOptions(),
): Translation = translate(texts = arrayOf(text), targetLang, sourceLang, options).translations.first()

suspend fun DeeplClient.translate(
    text: String,
    targetLang: TargetLang,
    sourceLang: SourceLang? = null,
    buildOptions: TranslateOptions.() -> Unit = {},
): Translation = translate(text, targetLang, sourceLang, TranslateOptions().apply(buildOptions))

suspend fun DeeplClient.translate(
    vararg texts: String,
    targetLang: TargetLang,
    sourceLang: SourceLang? = null,
    buildOptions: TranslateOptions.() -> Unit = {},
): TranslateResponse = translate(texts = texts, targetLang, sourceLang, TranslateOptions().apply(buildOptions))

suspend fun DeeplClient.translate(text: String, targetLang: TargetLang, sourceLang: SourceLang): Translation =
    translate(text, targetLang, sourceLang, options = TranslateOptions())

suspend fun DeeplClient.translate(vararg texts: String, targetLang: TargetLang, sourceLang: SourceLang): TranslateResponse =
    translate(texts = texts, targetLang, sourceLang, options = TranslateOptions())

suspend fun DeeplClient.translate(
    text: String,
    targetLang: TargetLang,
    options: TranslateOptions = TranslateOptions(),
): Translation =
    translate(text, targetLang, sourceLang = null, options)

suspend fun DeeplClient.translate(
    vararg texts: String,
    targetLang: TargetLang,
    options: TranslateOptions = TranslateOptions(),
): TranslateResponse =
    translate(texts = texts, targetLang, sourceLang = null, options)

suspend fun DeeplClient.translateText(
    text: String,
    targetLang: TargetLang,
    sourceLang: SourceLang? = null,
    options: TranslateOptions = TranslateOptions(),
): String = translate(text, targetLang, sourceLang, options).text

suspend fun DeeplClient.translateText(
    text: String,
    targetLang: TargetLang,
    sourceLang: SourceLang? = null,
    buildOptions: TranslateOptions.() -> Unit = {},
): String = translate(text, targetLang, sourceLang, buildOptions).text

suspend fun DeeplClient.translateText(
    vararg texts: String,
    targetLang: TargetLang,
    sourceLang: SourceLang? = null,
    buildOptions: TranslateOptions.() -> Unit = {},
): List<String> = translate(texts = texts, targetLang, sourceLang, buildOptions).translations.map(Translation::text)

suspend fun DeeplClient.translateText(text: String, targetLang: TargetLang, sourceLang: SourceLang): String =
    translate(text, targetLang, sourceLang).text

suspend fun DeeplClient.translateText(vararg texts: String, targetLang: TargetLang, sourceLang: SourceLang): List<String> =
    translate(texts = texts, targetLang, sourceLang).translations.map(Translation::text)

suspend fun DeeplClient.translateText(
    text: String,
    targetLang: TargetLang,
    options: TranslateOptions = TranslateOptions(),
): String =
    translate(text, targetLang, options).text

suspend fun DeeplClient.translateText(
    vararg texts: String,
    targetLang: TargetLang,
    options: TranslateOptions = TranslateOptions(),
): List<String> =
    translate(texts = texts, targetLang, options).translations.map(Translation::text)
