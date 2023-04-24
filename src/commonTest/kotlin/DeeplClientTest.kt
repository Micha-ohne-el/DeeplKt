package moe.micha.deeplkt

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk

class DeeplClientTest : DescribeSpec() {
    init {
        val paidAuthKey = "01234567-89AB-CDEF-0123-456789ABCDEF"
        val freeAuthKey = "$paidAuthKey:fx"

        lateinit var engineSpy: MockEngine

        beforeEach {
            engineSpy = MockEngine { request ->
                respondOk()
            }
        }

        describe("translate") {
            it("requires only text and targetLang arguments") {
                DeeplClient(paidAuthKey, engineSpy).translate(text = "text", targetLang = "targetLang")
            }

            context("request") {
                it("calls api.deepl.com") {
                    DeeplClient(paidAuthKey, engineSpy).translate("text", "targetLang")

                    engineSpy.requestHistory.forAll {
                        it.url.host shouldBe "api.deepl.com"
                    }
                }

                it("calls api-free.deepl.com for a free account") {
                    DeeplClient(freeAuthKey, engineSpy).translate("text", "targetLang")

                    engineSpy.requestHistory.forAll {
                        it.url.host shouldBe "api-free.deepl.com"
                    }
                }
            }
        }
    }
}
