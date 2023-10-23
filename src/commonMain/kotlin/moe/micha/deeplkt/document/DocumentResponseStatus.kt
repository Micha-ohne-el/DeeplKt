package moe.micha.deeplkt.document

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class DocumentResponseStatus {
    @SerialName("queued")
    Queued,

    @SerialName("translating")
    Translating,

    @SerialName("done")
    Done,

    @SerialName("error")
    Error;
}
