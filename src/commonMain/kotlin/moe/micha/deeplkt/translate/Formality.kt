package moe.micha.deeplkt.translate

/**
 * Whether the translated text should lean towards formal or informal language.
 * This doesn't work for all target languages.
 *
 * [More] and [Less] will fall back to [Default] if the target language doesn't support them.
 * [MoreOrFail] and [LessOrFail] will instead throw an exception.
 */
enum class Formality(
    val value: String,
) {
    Default("default"),
    More("prefer_more"),
    Less("prefer_less"),
    MoreOrFail("more"),
    LessOrFail("less")
}
