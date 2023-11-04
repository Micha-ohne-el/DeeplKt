package moe.micha.deeplkt.internal

import io.ktor.client.engine.java.Java

internal actual val defaultHttpClientEngine = Java.create()
