package moe.micha.deeplkt.document

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("status")
sealed class DocumentResponse {
    abstract val documentId: String

    @Serializable
    @SerialName("queued")
    data class Queued(
        @SerialName("document_id")
        override val documentId: String,

        @SerialName("seconds_remaining")
        private val secondsRemaining: Int? = null,
    ) : DocumentResponse(), InProgress {
        override val timeRemaining = secondsRemaining?.seconds
    }

    @Serializable
    @SerialName("translating")
    data class Translating(
        @SerialName("document_id")
        override val documentId: String,

        @SerialName("seconds_remaining")
        private val secondsRemaining: Int? = null,
    ) : DocumentResponse(), InProgress {
        override val timeRemaining = secondsRemaining?.seconds
    }

    @Serializable
    @SerialName("done")
    data class Done(
        @SerialName("document_id")
        override val documentId: String,

        @SerialName("billed_characters")
        val billedCharacters: Int? = null,
    ) : DocumentResponse()

    @Serializable
    @SerialName("error")
    data class Error(
        @SerialName("document_id")
        override val documentId: String,
        val message: String,
    ) : DocumentResponse()

    interface InProgress {
        val timeRemaining: Duration?
    }
}
