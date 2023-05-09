package moe.micha.deeplkt.translate

enum class Formality(
    val value: String,
) {
    Default("default"),
    More("prefer_more"),
    Less("prefer_less"),

    /**
     * Calling [moe.micha.deeplkt.DeeplClient.translate] with [MoreOrFail]
     * will throw an exception if the target language does not support formality.
     * TODO: What exception will be thrown?
     */
    MoreOrFail("more"),

    /**
     * Calling [moe.micha.deeplkt.DeeplClient.translate] with [LessOrFail]
     * will throw an exception if the target language does not support formality.
     * TODO: What exception will be thrown?
     */
    LessOrFail("less")
}
