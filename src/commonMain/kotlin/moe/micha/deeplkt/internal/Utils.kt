package moe.micha.deeplkt.internal

import io.ktor.client.request.forms.FormBuilder
import io.ktor.util.StringValuesBuilder

fun StringValuesBuilder.append(name: String, value: String?) {
    if (value != null) {
        append(name, value)
    }
}

fun FormBuilder.append(key: String, value: String?) {
    if (value != null) {
        append(key, value)
    }
}
