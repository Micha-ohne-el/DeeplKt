package moe.micha.deeplkt.document

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadDocumentResponse(
    @SerialName("document_id")
    val id: String,

    @SerialName("document_key")
    val key: String,
)
