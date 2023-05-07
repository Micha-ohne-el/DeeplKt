package moe.micha.deeplkt

enum class Formality(
    val value: String,
) {
    Default("default"),
    More("prefer_more"),
    Less("prefer_less"),

    /**
     * Calling [DeeplClient.translate] with [MoreOrFail]
     * will throw an exception if the target language does not support formality.
     * TODO: What exception will be thrown?
     */
    MoreOrFail("more"),

    /**
     * Calling [DeeplClient.translate] with [LessOrFail]
     * will throw an exception if the target language does not support formality.
     * TODO: What exception will be thrown?
     */
    LessOrFail("less")
}
