package dev.buijs.klutter.plugins.gradle.dsl


/**
 * @author Gillian Buijs
 */
@DslMarker
internal annotation class KlutterAppConfigDSLMarker

@KlutterAppConfigDSLMarker
class KlutterAppConfigDSL: KlutterDSL<KlutterAppConfigBuilder> {
    override fun configure(lambda: KlutterAppConfigBuilder.() -> Unit): KlutterAppConfigDTO {
        return KlutterAppConfigBuilder().apply(lambda).build()
    }
}

@KlutterAppConfigDSLMarker
class KlutterAppConfigBuilder: KlutterDSLBuilder {
    var name: String = ""

    override fun build() = KlutterAppConfigDTO(name = name)
}

/**
 * DTO for storing Kotlin IOS configuration.
 */
data class KlutterAppConfigDTO(val name: String): KlutterDTO