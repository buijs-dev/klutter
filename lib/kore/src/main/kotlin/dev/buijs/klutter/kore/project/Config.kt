package dev.buijs.klutter.kore.project

import com.fasterxml.jackson.annotation.JsonProperty

data class Config(
    @JsonProperty("dependencies")
    val dependencies: Dependencies? = null,

    @JsonProperty("bom-version")
    val bomVersion: String? = null,
)

data class Dependencies(
    @JsonProperty("klutter")
    val klutter: String? = null,

    @JsonProperty("klutter_ui")
    val klutterUI: String? = null,

    @JsonProperty("squint_json")
    val squint: String? = null,
)