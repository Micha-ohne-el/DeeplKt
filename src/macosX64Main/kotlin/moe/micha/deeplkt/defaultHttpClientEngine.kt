package moe.micha.deeplkt

import io.ktor.client.engine.darwin.Darwin

actual val defaultHttpClientEngine = Darwin.create()
