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
    val documentsUsed: Int,

    @SerialName("document_limit")
    val documentLimit: Int,

    @SerialName("team_document_count")
    val teamDocumentsUsed: Int,

    @SerialName("team_document_limit")
    val teamDocumentLimit: Int,
)
