package dev.buijs.klutter.plugins.gradle.dsl


/**
 * @author Gillian Buijs
 */
@DslMarker
internal annotation class AppConfigDSLMarker

@AppConfigDSLMarker
class AppConfigDSL: KlutterDSL<AppConfigBuilder> {
    override fun configure(lambda: AppConfigBuilder.() -> Unit): AppConfigDTO {
        return AppConfigBuilder().apply(lambda).build()
    }
}

@AppConfigDSLMarker
class AppConfigBuilder: KlutterDSLBuilder {
    var name: String = ""

    override fun build() = AppConfigDTO(name = name)
}

/**
 * DTO for storing Kotlin IOS configuration.
 */
data class AppConfigDTO(val name: String): KlutterDTO