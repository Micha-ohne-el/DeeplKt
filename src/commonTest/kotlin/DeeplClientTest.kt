package moe.micha.deeplkt

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import io.ktor.http.HttpMethod
import io.ktor.http.URLProtocol

class DeeplClientTest : DescribeSpec() {
    init {
        val authKey = "01234567-89AB-CDEF-0123-456789ABCDEF"
        val freeAuthKey = "$authKey:fx"

        lateinit var engineSpy: MockEngine

        beforeEach {
            engineSpy = MockEngine {
                respondOk()
            }
        }

        describe("translate") {
            context("request") {
                suspend fun send(key: String = authKey): MockEngine {
                    DeeplClient(key, engineSpy).translate(text = "text", targetLang = "targetLang")

                    return engineSpy
                }

                it("uses HTTPS") {
                    send().requestHistory.forAll {
                        it.url.protocol shouldBe URLProtocol.HTTPS
                    }
                }

                it("issues a POST request") {
                    send().requestHistory.forAll {
                        it.method shouldBe HttpMethod.Post
                    }
                }

                it("calls v2 endpoint") {
                    send().requestHistory.forAll {
                        it.url.encodedPath shouldBe "/v2/translate"
                    }
                }

                it("calls paid API for paid account") {
                    send().requestHistory.forAll {
                        it.url.host shouldBe "api.deepl.com"
                    }
                }

                it("calls free API for free account") {
                    send(freeAuthKey).requestHistory.forAll {
                        it.url.host shouldBe "api-free.deepl.com"
                    }
                }

                it("includes text to be translated") {
                    send().requestHistory.forAll {
                        it.url.parameters["text"] shouldBe "text"
                    }
                }
            }
        }
    }
}
