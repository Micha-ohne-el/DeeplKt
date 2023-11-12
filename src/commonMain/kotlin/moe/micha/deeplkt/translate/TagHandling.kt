package moe.micha.deeplkt.translate

/**
 * Tells DeepL to try to extract texts out of a structure, translate them individually, and place them back in the same
 * structure.
 *
 * Refer to the [official docs](https://www.deepl.com/docs-api/xml) for more info.
 */
enum class TagHandling(
    val value: String,
) {
    Xml("xml"),
    Html("html");
}
