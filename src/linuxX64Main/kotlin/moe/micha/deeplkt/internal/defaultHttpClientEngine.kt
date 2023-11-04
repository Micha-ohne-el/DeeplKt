package moe.micha.deeplkt.internal

import io.ktor.client.engine.curl.Curl

internal actual val defaultHttpClientEngine = Curl.create()
