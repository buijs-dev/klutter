package dev.buijs.klutter.kore.project

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import dev.buijs.klutter.kore.common.verifyExists
import mu.KotlinLogging
import java.io.File

private val log = KotlinLogging.logger { }

@JsonPropertyOrder(
    "bom-version",
    "dependencies")
data class Config(
    @JsonProperty("dependencies")
    val dependencies: Dependencies = Dependencies(),

    @JsonProperty("bom-version")
    val bomVersion: String = klutterKommanderVersion,
)

@JsonPropertyOrder(
    "klutter",
    "klutter_ui",
    "squint",
    "embedded")
data class Dependencies(
    @JsonProperty("klutter")
    val klutter: String = klutterPubVersion,

    @JsonProperty("klutter_ui")
    val klutterUI: String = klutterUIPubVersion,

    @JsonProperty("squint_json")
    val squint: String = squintPubVersion,

    @JsonProperty("embedded")
    val embedded: Set<String> = emptySet()
)

fun File.toConfigOrNull(): Config? = try {
    mapper.readValue(verifyExists().readText(), Config::class.java)
} catch(e: Exception) { log.error { e }; null }