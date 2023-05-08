package moe.micha.deeplkt

import io.ktor.client.engine.js.Js

actual val defaultHttpClientEngine = Js.create()
