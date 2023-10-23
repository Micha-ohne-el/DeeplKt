package moe.micha.deeplkt

import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.http.Headers
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

inline fun <reified T : Any> MockRequestHandleScope.respond(
    content: T,
    status: HttpStatusCode = HttpStatusCode.OK,
    headers: Headers = headersOf("Content-Type", "application/json"),
) =
    respond(Json.encodeToString(content), status, headers)
