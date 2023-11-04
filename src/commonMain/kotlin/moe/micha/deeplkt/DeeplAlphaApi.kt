package moe.micha.deeplkt

import kotlin.RequiresOptIn.Level.ERROR

@RequiresOptIn(
    "DeepL alpha features may break or be removed at any moment! Only use them for testing purposes!",
    level = ERROR,
)
annotation class DeeplAlphaApi

