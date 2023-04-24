package moe.micha.deeplkt

import io.ktor.client.engine.cio.CIO

actual val defaultHttpClientEngine = CIO.create()
