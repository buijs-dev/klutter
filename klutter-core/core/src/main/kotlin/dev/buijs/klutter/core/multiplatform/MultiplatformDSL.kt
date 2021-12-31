package dev.buijs.klutter.core.multiplatform

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
class MultiplatformDSL {
    fun configure(lambda: MultiplatformBuilder.() -> Unit): MultiplatformDTO {
        return MultiplatformBuilder().apply(lambda).build()
    }
}

@MultiplatformDSLMarker
class MultiplatformBuilder {
    var source: String? = null

    fun build() = MultiplatformDTO(source = source ?: throw KlutterMultiplatformException(noSourcesMessage))
}