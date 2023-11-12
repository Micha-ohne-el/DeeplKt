package moe.micha.deeplkt.translate

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import moe.micha.deeplkt.SourceLang

/**
 * @param detectedSourceLang The language of the translated content (either the specified one or the one that was detected).
 */
@Serializable
data class Translation(
    @SerialName("detected_source_language")
    val detectedSourceLang: SourceLang,
    val text: String,
)
