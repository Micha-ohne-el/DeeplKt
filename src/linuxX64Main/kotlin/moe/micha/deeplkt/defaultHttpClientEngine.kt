package moe.micha.deeplkt

import io.ktor.client.engine.curl.Curl

actual val defaultHttpClientEngine = Curl.create()
