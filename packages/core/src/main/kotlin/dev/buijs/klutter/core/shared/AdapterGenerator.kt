package dev.buijs.klutter.core.shared

import dev.buijs.klutter.core.FileWriter
import dev.buijs.klutter.core.KlutterFileGenerator
import dev.buijs.klutter.core.KlutterWriter
import java.io.File

/**
 * Parent for classes which generate method-channel boilerplate code.
 *
 * Implementing classes:
 * - [IosAdapterGenerator]
 * - [AndroidAdapter]
 * - [FlutterAdapterGenerator]
 */
internal abstract class AdapterGenerator(val data: AdapterData): KlutterFileGenerator() {

    /**
     * Generate the actual File.
     */
    override fun writer(): KlutterWriter =
        FileWriter(path(), printer().print())

    /**
     * Path to File which should be generated.
     */
    abstract fun path(): File

    /**
     * Method channel name which uses the package name
     * defined in the pubspec.yaml or
     * defaults to KLUTTER if not present.
     */
    open fun methodChannelName() = data.pubspec.android?.pluginPackage ?: "KLUTTER"

    /**
     * Filename of the to be generated classes extracted from the pubspec.yaml.
     *
     * Example:
     *
     * Given this pubspec.yaml:
     * ```
     *    flutter:
     *      plugins:
     *        platforms:
     *          android:
     *              package: com.empire.sucks.force_awakening
     *              pluginClass: ForceAwakeningPlugin
     *          ios:
     *              pluginClass: ForceAwakeningPlugin
     * ```
     *
     * androidPluginClassName returns 'ForceAwakeningPlugin'.
     *
     */
    open fun androidPluginClassName() = data.pubspec.android?.pluginClass

    /**
     * Filename of the to be generated classes extracted from the pubspec.yaml.
     *
     * Example:
     *
     * Given this pubspec.yaml:
     * ```
     *    flutter:
     *      plugins:
     *        platforms:
     *          android:
     *              package: com.empire.sucks.force_awakening
     *              pluginClass: ForceAwakeningPlugin
     *          ios:
     *              pluginClass: ForceAwakeningPlugin
     * ```
     *
     * iosPluginClassName returns 'ForceAwakeningPlugin'.
     *
     */
    open fun iosPluginClassName() = data.pubspec.ios?.pluginClass
}