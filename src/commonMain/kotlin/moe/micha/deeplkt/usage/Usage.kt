package moe.micha.deeplkt.usage

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Usage(
    @SerialName("character_count")
    val charactersUsed: Int,

    @SerialName("character_limit")
    val characterLimit: Int,

    @SerialName("document_count")
    val documentsUsed: Int? = null,

    @SerialName("document_limit")
    val documentLimit: Int? = null,

    @SerialName("team_document_count")
    val teamDocumentsUsed: Int? = null,

    @SerialName("team_document_limit")
    val teamDocumentLimit: Int? = null,
) {
    val charactersUsedRatio get() = charactersUsed.toDouble() / characterLimit.toDouble()
    val documentsUsedRatio get() = documentsUsed?.toDouble() / documentLimit?.toDouble()
    val teamDocumentsUsedRatio get() = teamDocumentsUsed?.toDouble() / teamDocumentLimit?.toDouble()

    val charactersAvailable get() = characterLimit - charactersUsed
    val documentsAvailable get() = documentLimit - documentsUsed
    val teamDocumentsAvailable get() = teamDocumentLimit - teamDocumentsUsed


    private operator fun Double?.div(that: Double?) =
        if (this == null || that == null) {
            null
        } else {
            this / that
        }

    private operator fun Int?.minus(that: Int?) =
        if (this == null || that == null) {
            null
        } else {
            this - that
        }
}
