package moe.micha.deeplkt.internal

import io.ktor.client.engine.darwin.Darwin

internal actual val defaultHttpClientEngine = Darwin.create()
