package moe.micha.deeplkt.internal

import io.ktor.http.HttpStatusCode

val HttpStatusCode.Companion.QuotaExceeded get() = HttpStatusCode(456, "Quota Exceeded")
