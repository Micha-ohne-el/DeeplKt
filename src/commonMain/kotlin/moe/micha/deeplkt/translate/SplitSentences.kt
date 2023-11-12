package moe.micha.deeplkt.translate

/**
 * Tells DeepL to split translation texts into sentences and then translate them individually.
 */
enum class SplitSentences(
    val value: String,
) {
    Never("0"),
    OnPunctuationAndNewlines("1"),

    @Suppress("SpellCheckingInspection")
    OnPunctuation("nonewlines");
}
