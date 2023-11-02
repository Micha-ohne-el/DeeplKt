package moe.micha.deeplkt

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = SourceLang.Serializer::class)
enum class SourceLang(
    val code: String,
) {
    Bulgarian("BG"),
    ChineseSimplified("ZH"),
    Czech("CS"),
    Danish("DA"),
    Dutch("NL"),
    English("EN"),
    Estonian("ET"),
    Finnish("FI"),
    French("FR"),
    German("DE"),
    Greek("EL"),
    Hungarian("HU"),
    Indonesian("ID"),
    Italian("IT"),
    Japanese("JA"),
    Korean("KO"),
    Latvian("LV"),
    Lithuanian("LT"),
    NorwegianBokmal("NB"),
    Polish("PL"),
    Portuguese("PT"),
    Romanian("RO"),
    Russian("RU"),
    Slovak("SK"),
    Slovenian("SL"),
    Spanish("ES"),
    Swedish("SV"),
    Turkish("TR"),
    Ukrainian("UK");

    object Serializer : KSerializer<SourceLang> {
        private val values = entries.associateBy(SourceLang::code)

        override val descriptor = PrimitiveSerialDescriptor("SourceLang", STRING)

        override fun serialize(encoder: Encoder, value: SourceLang) = encoder.encodeString(value.code)

        override fun deserialize(decoder: Decoder) = values[decoder.decodeString()]!!
    }
}
