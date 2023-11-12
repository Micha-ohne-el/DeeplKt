package moe.micha.deeplkt.translate

/**
 * [Yes] tells DeepL to try to keep the formatting of the original text, even if it is detected as incorrect by DeepL.
 * [No] tells DeepL to try to correct formatting mistakes.
 */
enum class PreserveFormatting(
    val value: String,
) {
    No("0"),
    Yes("1");
}
