package dev.buijs.klutter.core.shared

import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.DartEnum
import dev.buijs.klutter.core.DartMessage
import dev.buijs.klutter.core.Method
import dev.buijs.klutter.core.annotations.KlutterAdapteeScanner
import dev.buijs.klutter.core.annotations.KlutterResponseProcessor
import dev.buijs.klutter.core.annotations.ReturnTypeLanguage

/**
 * Utility to scan a Klutter project and collect it's metadata.
 *
 * The metadata is required to generate method channel code in:
 * - root/lib
 * - root/android
 * - root/ios
 */
internal class AdapterCollector(

    /**
     * The root folder containing the plugin code where the code should be generated.
     */
    root: Root,

    /**
     * The root/platform module containing the Kotlin Multiplatform code where the code should be scanned.
     */
    platform: Platform,
) {

    // Get the path to the root/platform/src/commonMain folder.
    private val source = platform.source()

    // Parse the pubspec.yaml.
    private val pubspec = root.toPubspecData()

    // Scan platform module for @KlutterAdaptee.
    private val methods = KlutterAdapteeScanner(source)
        .scan(language = ReturnTypeLanguage.DART)

    // Scan for any class annotated with @KlutterResponse.
    private val processor = KlutterResponseProcessor(source)

    // Response classes annotated with @KlutterResponse.
    private val messages = processor.messages

    // Enumerations annotated with @KlutterResponse.
    private val enumerations = processor.enumerations

    val data = AdapterData(
        pubspec = pubspec,
        methods = methods,
        messages = messages,
        enumerations = enumerations,
    )
}

/**
 * Metadata containing all required information to generated method channel code.
 */
internal data class AdapterData(

    /**
     * The parsed pubspec.yaml containing plugin configuration.
     */
    val pubspec: PubspecData,

    /**
     * List of @KlutterAdaptee annotated methods.
     */
    val methods: List<Method>,

    /**
     * List of custom data transfer objects annotated with @KlutterResponse defined in the platform module.
     */
    val messages: List<DartMessage>,

    /**
     * List of enumerations annotated with @KlutterResponse defined in the platform module.
     */
    val enumerations: List<DartEnum>,
)