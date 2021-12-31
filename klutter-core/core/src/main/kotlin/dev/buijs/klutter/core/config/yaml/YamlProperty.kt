package dev.buijs.klutter.core.config.yaml

data class YamlProperty(
    val key: String,
    val value: String,
    val type: YamlPropertyType
)

enum class YamlPropertyType {
    Int, String
}