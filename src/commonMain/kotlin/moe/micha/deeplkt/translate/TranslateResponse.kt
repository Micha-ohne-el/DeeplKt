package moe.micha.deeplkt.translate

import kotlinx.serialization.Serializable

@Serializable
data class TranslateResponse(
    val translations: List<Translation>,
) {
    constructor(vararg translation: Translation) : this(translation.asList())
}
