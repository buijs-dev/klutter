package dev.buijs.klutter.core.config.yaml

data class KlutterYamlProperty(
    val key: String,
    val value: String,
    val type: KlutterYamlPropertyType
)

enum class KlutterYamlPropertyType {
    Int, String
}