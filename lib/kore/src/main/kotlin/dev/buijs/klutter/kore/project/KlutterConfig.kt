package dev.buijs.klutter.kore.project

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.File

data class KlutterConfig(
    @JsonProperty("dependencies")
    val dependencies: Dependencies? = null,
)

data class Dependencies(
    @JsonProperty("klutter")
    val klutter: String? = null,

    @JsonProperty("klutter_ui")
    val klutterUi: String? = null,

    @JsonProperty("squint_json")
    val squint: String? = null,
)

fun File.toKlutterConfig(): KlutterConfig = this
    .readText()
    .let { mapper.readValue(it, KlutterConfig::class.java) }