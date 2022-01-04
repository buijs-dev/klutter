package dev.buijs.klutter.gradle.dsl

/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 *
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
 * DTO for storing Kotlin IOS configuration.
 */
data class KlutterIosDTO(val version: String): KlutterDTO