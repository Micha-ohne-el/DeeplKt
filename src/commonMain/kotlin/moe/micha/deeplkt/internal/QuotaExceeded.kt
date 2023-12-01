package moe.micha.deeplkt.internal

import io.ktor.http.HttpStatusCode

internal val HttpStatusCode.Companion.QuotaExceeded get() = HttpStatusCode(456, "Quota Exceeded")
