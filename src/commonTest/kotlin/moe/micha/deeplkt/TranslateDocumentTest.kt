package moe.micha.deeplkt

import io.kotest.assertions.throwables.shouldThrowMessage
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.MultiPartFormDataContent
import moe.micha.deeplkt.document.DocumentResponse
import moe.micha.deeplkt.document.DocumentResponse.Done
import moe.micha.deeplkt.document.DocumentResponse.Queued
import moe.micha.deeplkt.document.DocumentResponse.Translating
import moe.micha.deeplkt.document.UploadDocumentResponse
import moe.micha.deeplkt.document.DocumentResponse.Error as Err

class TranslateDocumentTest : StringSpec() {
    private val documentId = "test document id"
    private val documentKey = "test document key"
    private val successfulResult = "test result"

    init {
        beforeEach {
            responses = mutableMapOf(
                "/v2/document" to {
                    respond(UploadDocumentResponse(documentId, key = documentKey))
                },
                "/v2/document/$documentId" to {
                    respond<DocumentResponse>(Done(documentId))
                },
                "/v2/document/$documentId/result" to {
                    respond(successfulResult)
                }
            )

            engineSpy = MockEngine {
                val response = responses[it.url.encodedPath]

                if (response != null) {
                    response(this@MockEngine, it)
                } else {
                    respondBadRequest()
                }
            }

            client = DeeplClient(authKey, engineSpy)
        }

        "uploads document" {
            client.translateDocument(content = "test content", fileName = "test.txt", to = TargetLang.Dutch)

            val request = engineSpy.requestHistory[0]
            request.url.encodedPath shouldBe "/v2/document"

            val formData = request.multiFormBody.toByteArray().decodeToString()
            formData shouldContain "test content"
            formData shouldContain TargetLang.Dutch.code
            formData shouldContain "test.txt"
        }

        "checks status of document" {
            client.translateDocumentText(content = "test content", fileName = "test.txt", to = TargetLang.Dutch)

            val request = engineSpy.requestHistory[1]
            request.url.encodedPath shouldBe "/v2/document/$documentId"

            val formData = request.formBody.formData
            formData["document_key"] shouldBe documentKey
        }

        "continues checking status until done" {
            val statusResponses = listOf(
                Queued(documentId),
                Queued(documentId, secondsRemaining = 20),
                Translating(documentId, secondsRemaining = 10),
                Translating(documentId),
                Done(documentId),
            )
            val iterator = statusResponses.iterator()
            responses["/v2/document/$documentId"] = {
                respond<DocumentResponse>(iterator.next())
            }

            client.translateDocumentText(content = "test content", fileName = "test.txt", to = TargetLang.Dutch)

            val requests = engineSpy.requestHistory.slice(1..5)
            requests.forAll { request ->
                request.url.encodedPath shouldBe "/v2/document/$documentId"

                val formData = request.formBody.formData
                formData["document_key"] shouldBe documentKey
            }

            engineSpy.requestHistory[6].url.encodedPath shouldNotBe "/v2/document/$documentId"
        }

        "throws upon error response" {
            val statusResponses = listOf(
                Queued(documentId),
                Queued(documentId, secondsRemaining = 20),
                Translating(documentId, secondsRemaining = 10),
                Translating(documentId),
                Err(documentId, message = "test error message"),
            )
            val iterator = statusResponses.iterator()
            responses["/v2/document/$documentId"] = {
                respond<DocumentResponse>(iterator.next())
            }

            shouldThrowMessage("test error message") {
                client.translateDocumentText(content = "test content", fileName = "test.txt", to = TargetLang.Dutch)
            }

            val requests = engineSpy.requestHistory.slice(1..5)
            requests.forAll { request ->
                request.url.encodedPath shouldBe "/v2/document/$documentId"

                val formData = request.formBody.formData
                formData["document_key"] shouldBe documentKey
            }

            engineSpy.requestHistory.size shouldBe 6
        }

        "downloads translation upon success" {
            val result =
                client.translateDocumentText(content = "test content", fileName = "test.txt", to = TargetLang.Dutch)

            result shouldBe successfulResult

            val request = engineSpy.requestHistory[2]
            request.url.encodedPath shouldBe "/v2/document/$documentId/result"

            val formData = request.formBody.formData
            formData["document_key"] shouldBe documentKey
        }
    }

    private val authKey get() = "01234567-89AB-CDEF-0123-456789ABCDEF"

    private lateinit var client: DeeplClient
    private lateinit var engineSpy: MockEngine
    private lateinit var responses: MutableMap<String, suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData>

    private val HttpRequestData.formBody get() = body as FormDataContent
    private val HttpRequestData.multiFormBody get() = body as MultiPartFormDataContent
}
