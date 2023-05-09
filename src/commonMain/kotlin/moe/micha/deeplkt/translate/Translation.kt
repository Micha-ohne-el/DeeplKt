package moe.micha.deeplkt.translate

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import moe.micha.deeplkt.SourceLang

@Serializable
data class Translation(
    @SerialName("detected_source_language")
    val detectedSourceLang: SourceLang,
    val text: String,
)
