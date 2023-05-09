package moe.micha.deeplkt

import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.headers
import moe.micha.deeplkt.translate.Formality
import moe.micha.deeplkt.translate.OutlineDetection
import moe.micha.deeplkt.translate.PreserveFormatting
import moe.micha.deeplkt.translate.SplitSentences
import moe.micha.deeplkt.translate.TagHandling
import moe.micha.deeplkt.translate.TranslateResponse
import moe.micha.deeplkt.translate.Translation

class TranslateTest : StringSpec() {
    init {
        beforeEach {
            engineSpy = MockEngine {
                respond(
                    """{"translations":[{"detected_source_language":"EN","text":"abc"}]}""",
                    HttpStatusCode.OK,
                    headers = headers {
                        append("Content-Type", "application/json")
                    }
                )
            }
            client = DeeplClient(authKey, engineSpy)
        }

        "uses HTTPS" {
            send().requestHistory.forAll {
                it.url.protocol shouldBe URLProtocol.HTTPS
            }
        }

        "issues a POST request" {
            send().requestHistory.forAll {
                it.method shouldBe HttpMethod.Post
            }
        }

        "calls v2 endpoint" {
            send().requestHistory.forAll {
                it.url.encodedPath shouldBe "/v2/translate"
            }
        }

        "calls paid API for paid account" {
            send().requestHistory.forAll {
                it.url.host shouldBe "api.deepl.com"
            }
        }

        "calls free API for free account" {
            send(freeAuthKey).requestHistory.forAll {
                it.url.host shouldBe "api-free.deepl.com"
            }
        }

        "request contains auth key" {
            send().requestHistory.forAll {
                it.headers["Authorization"] shouldBe "DeepL-Auth-Key $authKey"
            }
        }

        "parameters include targetLang" {
            client.translate("", targetLang = TargetLang.Dutch)

            engineSpy.requestHistory.forAll {
                it.formBody.formData["target_lang"] shouldBe "NL"
            }
        }

        "parameters include sourceLang" {
            client.translate("", TargetLang.Dutch, sourceLang = SourceLang.French)

            engineSpy.requestHistory.forAll {
                it.formBody.formData["source_lang"] shouldBe "FR"
            }
        }

        "parameters include splitSentences" {
            client.translate("", TargetLang.Dutch, splitSentences = SplitSentences.Never)

            engineSpy.requestHistory.forAll {
                it.formBody.formData["split_sentences"] shouldBe "0"
            }
        }

        "parameters include preserveFormatting" {
            client.translate("", TargetLang.Dutch, preserveFormatting = PreserveFormatting.Yes)

            engineSpy.requestHistory.forAll {
                it.formBody.formData["preserve_formatting"] shouldBe "1"
            }
        }

        "parameters include formality" {
            client.translate("", TargetLang.Dutch, formality = Formality.More)

            engineSpy.requestHistory.forAll {
                it.formBody.formData["formality"] shouldBe "prefer_more"
            }
        }

        "parameters include tagHandling" {
            client.translate("", TargetLang.Dutch, tagHandling = TagHandling.Xml)

            engineSpy.requestHistory.forAll {
                it.formBody.formData["tag_handling"] shouldBe "xml"
            }
        }

        "parameters include nonSplittingTags" {
            client.translate("", TargetLang.Dutch, nonSplittingTags = setOf("one", "two"))
            client.translate("", TargetLang.Dutch, nonSplittingTags = listOf("one", "two"))

            engineSpy.requestHistory.forAll {
                it.formBody.formData["non_splitting_tags"] shouldMatch Regex("one,two|two,one")
            }
        }

        "parameters include outlineDetection" {
            client.translate("", TargetLang.Dutch, outlineDetection = OutlineDetection.Disabled)

            engineSpy.requestHistory.forAll {
                it.formBody.formData["outline_detection"] shouldBe "0"
            }
        }

        "parameters include splittingTags" {
            client.translate("", TargetLang.Dutch, splittingTags = setOf("test1", "test2"))
            client.translate("", TargetLang.Dutch, splittingTags = listOf("test1", "test2"))

            engineSpy.requestHistory.forAll {
                it.formBody.formData["splitting_tags"] shouldMatch Regex("test1,test2|test2,test1")
            }
        }

        "parameters include ignoreTags" {
            client.translate("", TargetLang.Dutch, ignoreTags = setOf("abc", "def"))
            client.translate("", TargetLang.Dutch, ignoreTags = listOf("abc", "def"))

            engineSpy.requestHistory.forAll {
                it.formBody.formData["ignore_tags"] shouldMatch Regex("abc,def|def,abc")
            }
        }

        "can be called with single text" {
            val result = client.translate("text", TargetLang.Dutch)

            engineSpy.requestHistory.forAll {
                it.formBody.formData.getAll("text") shouldBe listOf("text")
            }

            result.shouldBeInstanceOf<Translation>()
        }

        "can be called with multiple texts" {
            val result = client.translate("text1", "text2", "text3", targetLang = TargetLang.Dutch)

            engineSpy.requestHistory.forAll {
                it.formBody.formData.getAll("text") shouldBe listOf("text1", "text2", "text3")
            }

            result.shouldBeInstanceOf<TranslateResponse>()
        }

        "result texts are ordered correctly" {
            engineSpy = MockEngine {
                respond(
                    """
                        {"translations":[
                            {"detected_source_language":"EN","text":"first text"},
                            {"detected_source_language":"EN","text":"second text"}
                        ]}
                    """.trimIndent(),
                    HttpStatusCode.OK,
                    headers = headers {
                        append("Content-Type", "application/json")
                    }
                )
            }

            val result = DeeplClient(authKey, engineSpy).translate("", "", targetLang = TargetLang.Dutch)

            result.translations[0].text shouldBe "first text"
            result.translations[1].text shouldBe "second text"
        }
    }

    private val authKey get() = "01234567-89AB-CDEF-0123-456789ABCDEF"
    private val freeAuthKey get() = "$authKey:fx"

    private lateinit var client: DeeplClient
    private lateinit var engineSpy: MockEngine

    private val HttpRequestData.formBody get() = body as FormDataContent

    private suspend fun send(key: String = authKey): MockEngine {
        DeeplClient(key, engineSpy).translate(text = "text", targetLang = TargetLang.AmericanEnglish)

        return engineSpy
    }
}
