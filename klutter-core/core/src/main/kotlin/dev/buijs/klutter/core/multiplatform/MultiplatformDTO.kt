package dev.buijs.klutter.core.multiplatform

/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 *
 * DTO for storing Kotlin Multiplatform configuration.
 * Used by adapter plugin to find the common sourcecode, .aar file for android and podspec for ios.
 */
data class MultiplatformDTO(
    val source: String
)
