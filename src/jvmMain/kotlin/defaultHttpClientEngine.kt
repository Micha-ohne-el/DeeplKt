package moe.micha.deeplkt

import io.ktor.client.engine.java.Java

actual val defaultHttpClientEngine = Java.create()
