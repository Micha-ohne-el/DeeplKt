package moe.micha.deeplkt.translate

import io.ktor.http.parameters
import moe.micha.deeplkt.internal.append

data class TranslateOptions(
    var splitSentences: SplitSentences? = null,
    var preserveFormatting: PreserveFormatting? = null,
    var formality: Formality? = null,
    var tagHandling: TagHandling? = null,
    var nonSplittingTags: Iterable<String>? = null,
    var outlineDetection: OutlineDetection? = null,
    var splittingTags: Iterable<String>? = null,
    var ignoreTags: Iterable<String>? = null,
) {
    fun toParameters() = parameters {
        append("split_sentences", splitSentences?.value)
        append("preserve_formatting", preserveFormatting?.value)
        append("formality", formality?.value)
        append("tag_handling", tagHandling?.value)
        append("non_splitting_tags", nonSplittingTags?.joinToString(","))
        append("outline_detection", outlineDetection?.value)
        append("splitting_tags", splittingTags?.joinToString(","))
        append("ignore_tags", ignoreTags?.joinToString(","))
    }
}
