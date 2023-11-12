package moe.micha.deeplkt.translate

/**
 * [Enabled] tells DeepL to try to auto-detect the structure of XML input and try to figure out which pieces of text may belong
 * together.
 * [Disabled] disables this, so you can have more control over tag handling with [TagHandling], [SplitSentences],
 * and [TranslateOptions.splittingTags].
 */
enum class OutlineDetection(
    val value: String,
) {
    Disabled("0"),
    Enabled("1");
}
