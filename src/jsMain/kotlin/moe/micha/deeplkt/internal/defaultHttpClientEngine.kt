package moe.micha.deeplkt.internal

import io.ktor.client.engine.js.Js

internal actual val defaultHttpClientEngine = Js.create()
