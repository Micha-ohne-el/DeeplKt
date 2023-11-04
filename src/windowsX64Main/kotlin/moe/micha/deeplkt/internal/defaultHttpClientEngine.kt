package moe.micha.deeplkt.internal

import io.ktor.client.engine.winhttp.WinHttp

internal actual val defaultHttpClientEngine = WinHttp.create()
