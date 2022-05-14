package dev.buijs.klutter.plugins.gradle.dsl

/**
 * @author Gillian Buijs
 */
@DslMarker
internal annotation class KlutterAppInfoDSLMarker

@KlutterAppInfoDSLMarker
class KlutterAppInfoDSL: KlutterDSL<KlutterAppInfoBuilder> {
    override fun configure(lambda: KlutterAppInfoBuilder.() -> Unit): KlutterAppInfoDTO {
        return KlutterAppInfoBuilder().apply(lambda).build()
    }
}

@KlutterAppInfoDSLMarker
class KlutterAppInfoBuilder: KlutterDSLBuilder {

    var name: String = ""
    var version: String = ""
    var description: String = ""
    var projectFolder = ""
    var outputFolder = ""

    override fun build(): KlutterAppInfoDTO {
        return KlutterAppInfoDTO(
            name = name,
            version = version,
            appId = "dev.buijs.klutter.activity",
            description = description,
            projectFolder = projectFolder,
            outputFolder = outputFolder,
        )
    }


}

/**
 * DTO for storing shared app configuration.
 */
data class KlutterAppInfoDTO(
    val name: String,
    var version: String,
    val appId: String,
    val description: String,
    val projectFolder: String,
    val outputFolder: String,
): KlutterDTO

class KlutterAppInfoException(msg: String): Exception(msg)