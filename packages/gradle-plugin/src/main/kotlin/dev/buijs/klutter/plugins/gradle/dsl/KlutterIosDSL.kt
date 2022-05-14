package dev.buijs.klutter.plugins.gradle.dsl


/**
 * @author Gillian Buijs
 */
@DslMarker
internal annotation class KlutterIosDSLMarker

@KlutterIosDSLMarker
class KlutterIosDSL: KlutterDSL<KlutterIosBuilder> {
    override fun configure(lambda: KlutterIosBuilder.() -> Unit): KlutterIosDTO {
        return KlutterIosBuilder().apply(lambda).build()
    }
}

@KlutterIosDSLMarker
class KlutterIosBuilder: KlutterDSLBuilder {
    var version: String = "13.0"

    override fun build() = KlutterIosDTO(version = version)
}

/**
 * DTO for storing iOS specific configuration.
 */
data class KlutterIosDTO(val version: String): KlutterDTO