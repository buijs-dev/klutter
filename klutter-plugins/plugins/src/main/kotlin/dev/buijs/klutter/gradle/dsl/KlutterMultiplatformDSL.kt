package dev.buijs.klutter.gradle.dsl

import dev.buijs.klutter.core.KlutterMultiplatformException

/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 *
 */
private const val noSourcesMessage  = """
        No source directory found in Multiplatform.
        Please add 'source' field to multiplatform configuration.
        
        Example:
        'multiplatform {
            source = "klutter-root\kmp\common"
        }'
        """

@DslMarker
internal annotation class MultiplatformDSLMarker

@MultiplatformDSLMarker
class KlutterMultiplatformDSL: KlutterDSL<KlutterMultiplatformBuilder> {
    override fun configure(lambda: KlutterMultiplatformBuilder.() -> Unit): KlutterMultiplatformDTO {
        return KlutterMultiplatformBuilder().apply(lambda).build()
    }
}

@MultiplatformDSLMarker
class KlutterMultiplatformBuilder: KlutterDSLBuilder {
    var source: String? = null

    override fun build() = KlutterMultiplatformDTO(source = source ?: throw KlutterMultiplatformException(noSourcesMessage))
}

/**
 * DTO for storing Kotlin Multiplatform configuration.
 * Used by adapter plugin to find the common sourcecode, .aar file for android and podspec for ios.
 */
data class KlutterMultiplatformDTO(val source: String): KlutterDTO