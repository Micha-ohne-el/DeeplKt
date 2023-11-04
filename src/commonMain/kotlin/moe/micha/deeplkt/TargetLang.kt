package moe.micha.deeplkt

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = TargetLang.Serializer::class)
enum class TargetLang(
    val code: String,
) {
    AmericanEnglish("EN-US"),
    BrazilianPortuguese("PT-BR"),
    BritishEnglish("EN-GB"),
    Bulgarian("BG"),
    ChineseSimplified("ZH"),
    Czech("CS"),
    Danish("DA"),
    Dutch("NL"),
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
    Portuguese("PT-PT"),
    Romanian("RO"),
    Russian("RU"),
    Slovak("SK"),
    Slovenian("SL"),
    Spanish("ES"),
    Swedish("SV"),
    Turkish("TR"),
    Ukrainian("UK");

    internal object Serializer : KSerializer<TargetLang> {
        private val values = entries.associateBy(TargetLang::code)

        override val descriptor = PrimitiveSerialDescriptor("TargetLang", STRING)

        override fun serialize(encoder: Encoder, value: TargetLang) = encoder.encodeString(value.code)

        override fun deserialize(decoder: Decoder) = values[decoder.decodeString()]!!
    }
}
