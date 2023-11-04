package moe.micha.deeplkt

import kotlin.RequiresOptIn.Level.ERROR

@RequiresOptIn(
    "DeepL beta features may break or be removed at any moment! Only use them for testing purposes!",
    level = ERROR,
)
annotation class DeeplBetaApi
