package moe.micha.deeplkt

import io.ktor.client.engine.winhttp.WinHttp

actual val defaultHttpClientEngine = WinHttp.create()
