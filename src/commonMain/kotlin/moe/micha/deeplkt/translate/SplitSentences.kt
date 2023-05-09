package moe.micha.deeplkt.translate

enum class SplitSentences(
    val value: String,
) {
    Never("0"),
    OnPunctuationAndNewlines("1"),

    @Suppress("SpellCheckingInspection")
    OnPunctuation("nonewlines");
}
