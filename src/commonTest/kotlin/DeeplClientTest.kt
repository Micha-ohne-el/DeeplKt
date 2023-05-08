package moe.micha.deeplkt

import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import io.ktor.http.HttpMethod
import io.ktor.http.URLProtocol

class DeeplClientTest : StringSpec() {
    init {
        val authKey = "01234567-89AB-CDEF-0123-456789ABCDEF"
        val freeAuthKey = "$authKey:fx"

        lateinit var client: DeeplClient
        lateinit var engineSpy: MockEngine

        suspend fun send(key: String = authKey): MockEngine {
            DeeplClient(key, engineSpy).translate(text = "text", targetLang = TargetLang.AmericanEnglish)

            return engineSpy
        }

        beforeEach {
            engineSpy = MockEngine {
                respondOk()
            }
            client = DeeplClient(authKey, engineSpy)
        }

        "translate > request > uses HTTPS" {
            send().requestHistory.forAll {
                it.url.protocol shouldBe URLProtocol.HTTPS
            }
        }

        "translate > request > issues a POST request" {
            send().requestHistory.forAll {
                it.method shouldBe HttpMethod.Post
            }
        }

        "translate > request > calls v2 endpoint" {
            send().requestHistory.forAll {
                it.url.encodedPath shouldBe "/v2/translate"
            }
        }

        "translate > request > calls paid API for paid account" {
            send().requestHistory.forAll {
                it.url.host shouldBe "api.deepl.com"
            }
        }

        "translate > request > calls free API for free account" {
            send(freeAuthKey).requestHistory.forAll {
                it.url.host shouldBe "api-free.deepl.com"
            }
        }

        "translate > parameters > accepts targetLang parameter" {
            client.translate("", targetLang = TargetLang.Dutch)

            engineSpy.requestHistory.forAll {
                it.url.parameters["target_lang"] shouldBe "NL"
            }
        }

        "translate > parameters > accepts sourceLang parameter" {
            client.translate("", TargetLang.Dutch, sourceLang = SourceLang.French)

            engineSpy.requestHistory.forAll {
                it.url.parameters["source_lang"] shouldBe "FR"
            }
        }

        "translate > parameters > accepts splitSentences parameter" {
            client.translate("", TargetLang.Dutch, splitSentences = SplitSentences.Never)

            engineSpy.requestHistory.forAll {
                it.url.parameters["split_sentences"] shouldBe "0"
            }
        }

        "translate > parameters > accepts preserveFormatting parameter" {
            client.translate("", TargetLang.Dutch, preserveFormatting = PreserveFormatting.Yes)

            engineSpy.requestHistory.forAll {
                it.url.parameters["preserve_formatting"] shouldBe "1"
            }
        }

        "translate > parameters > accepts formality parameter" {
            client.translate("", TargetLang.Dutch, formality = Formality.More)

            engineSpy.requestHistory.forAll {
                it.url.parameters["formality"] shouldBe "prefer_more"
            }
        }

        "translate > parameters > accepts tagHandling parameter" {
            client.translate("", TargetLang.Dutch, tagHandling = TagHandling.Xml)

            engineSpy.requestHistory.forAll {
                it.url.parameters["tag_handling"] shouldBe "xml"
            }
        }

        "translate > parameters > accepts nonSplittingTags parameter" {
            client.translate("", TargetLang.Dutch, nonSplittingTags = setOf("one", "two"))
            client.translate("", TargetLang.Dutch, nonSplittingTags = listOf("one", "two"))

            engineSpy.requestHistory.forAll {
                it.url.parameters["non_splitting_tags"] shouldMatch """one,two|two,one"""
            }
        }

        "translate > parameters > accepts outlineDetection parameter" {
            client.translate("", TargetLang.Dutch, outlineDetection = OutlineDetection.Disabled)

            engineSpy.requestHistory.forAll {
                it.url.parameters["outline_detection"] shouldBe "0"
            }
        }

        "translate > parameters > accepts splittingTags parameter" {
            client.translate("", TargetLang.Dutch, splittingTags = setOf("test1", "test2"))
            client.translate("", TargetLang.Dutch, splittingTags = listOf("test1", "test2"))

            engineSpy.requestHistory.forAll {
                it.url.parameters["splitting_tags"] shouldMatch """test1,test2|test2,test1"""
            }
        }

        "translate > parameters > accepts ignoreTags parameter" {
            client.translate("", TargetLang.Dutch, ignoreTags = setOf("abc", "def"))
            client.translate("", TargetLang.Dutch, ignoreTags = listOf("abc", "def"))

            engineSpy.requestHistory.forAll {
                it.url.parameters["ignore_tags"] shouldMatch """abc,def|def,abc"""
            }
        }

        "translate > parameters > accepts multiple texts" {
            client.translate("text1", "text2", "text3", targetLang = TargetLang.Dutch)

            engineSpy.requestHistory.forAll {
                it.url.parameters.getAll("text") shouldBe listOf("text1", "text2", "text3")
            }
        }
    }
}
