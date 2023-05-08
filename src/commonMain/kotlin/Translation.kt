package moe.micha.deeplkt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Translation(
    @SerialName("detected_source_language")
    val detectedSourceLang: SourceLang,
    val text: String,
)
