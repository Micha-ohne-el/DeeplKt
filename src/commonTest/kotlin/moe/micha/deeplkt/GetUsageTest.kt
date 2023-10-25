package moe.micha.deeplkt

import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpMethod
import io.ktor.http.URLProtocol
import moe.micha.deeplkt.usage.Usage

class GetUsageTest : StringSpec() {
    init {
        beforeEach {
            engineSpy = MockEngine {
                respond(
                    Usage(charactersUsed = 123456, characterLimit = 654321),
                )
            }
        }

        "uses HTTPS" {
            send().requestHistory.forAll {
                it.url.protocol shouldBe URLProtocol.HTTPS
            }
        }

        "issues a GET request" {
            send().requestHistory.forAll {
                it.method shouldBe HttpMethod.Get
            }
        }

        "calls v2 endpoint" {
            send().requestHistory.forAll {
                it.url.encodedPath shouldBe "/v2/usage"
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

        "result should be valid Usage" {
            val usage = DeeplClient(authKey, engineSpy).getUsage()

            usage.shouldBeInstanceOf<Usage>()
            usage.charactersUsed shouldBe 123456
            usage.characterLimit shouldBe 654321
        }
    }

    private val authKey get() = "01234567-89AB-CDEF-0123-456789ABCDEF"
    private val freeAuthKey get() = "$authKey:fx"

    private lateinit var engineSpy: MockEngine

    private suspend fun send(key: String = authKey): MockEngine {
        DeeplClient(key, engineSpy).getUsage()

        return engineSpy
    }
}
