package moe.micha.deeplkt

import kotlinx.serialization.Serializable

@Serializable
data class TranslateResponse(
    val translations: List<Translation>,
)
