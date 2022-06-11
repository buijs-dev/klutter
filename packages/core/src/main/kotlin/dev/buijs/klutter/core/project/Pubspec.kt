@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package dev.buijs.klutter.core.project

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import dev.buijs.klutter.core.KlutterException
import dev.buijs.klutter.core.verifyExists
import java.io.File

internal fun Root.toPubspec(): Pubspec =
    folder.resolve("pubspec.yaml").toPubspec()

internal fun File.toPubspec(): Pubspec {

    verifyExists()

    val mapper = ObjectMapper(YAMLFactory()).also {
        it.registerKotlinModule()
        it.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
    }

    try {
        return mapper.readValue(readText(), Pubspec::class.java)
    } catch(e: Exception) {
        throw KlutterException("Failed to parse pubspec.yaml: ${e.message}")
    }
}

/**
 * The parsed pubspec.yaml file.
 *
 * Example:
 *
 *  ```
 *  name: ridiculous_plugin
 *  description: A new flutter plugin project.
 *  version: 0.0.1
 *  homepage: www.somehomepage.com
 *
 *  environment:
 *      sdk: ">=2.16.1 <3.0.0"
 *      flutter: ">=2.5.0"
 *
 *  dependencies:
 *      flutter:
 *          sdk: flutter
 *
 *  dev_dependencies:
 *      flutter_test:
 *      sdk: flutter
 *      flutter_lints: ^1.0.0
 *
 *  # For information on the generic Dart part of this file, see the
 *  # following page: https://dart.dev/tools/pub/pubspec
 *
 *  # The following section is specific to Flutter.
 *  flutter:
 *  # This section identifies this Flutter project as a plugin project.
 *  # The 'pluginClass' and Android 'package' identifiers should not ordinarily
 *  # be modified. They are used by the tooling to maintain consistency when
 *  # adding or updating assets for this project.
 *  plugin:
 *      platforms:
 *          android:
 *              package: some.company.ridiculous_plugin
 *              pluginClass: RidiculousPlugin
 *          ios:
 *              pluginClass: RidiculousPlugin
 *
 *  assets:
 *    - images/a_dot_burr.jpeg
 *    - images/a_dot_ham.jpeg
 *
 *  ```
 */
@JsonIgnoreProperties(ignoreUnknown = true)
internal data class Pubspec(

    @JsonProperty("name")
    val name: String,

    @JsonProperty("flutter")
    internal val flutter: PubspecFlutter?,
) {

    /**
     * The plugin content.
     */
    val plugin: PubspecPlugin?
        get() = flutter?.plugin

    /**
     * The supported plugin platforms.
     *
     * In the context of Klutter this is only iOS and Android.
     */
    val platforms: PubspecPluginPlatforms?
        get() = plugin?.platforms

    /**
     * The Android plugin class name.
     */
    val android: PubspecPluginClass?
        get() = platforms?.android

    /**
     * The IOS plugin class name.
     */
    val ios: PubspecPluginClass?
        get() = platforms?.ios
}

/**
 * flutter:
 *  plugin:
 *      platforms:
 *          android:
 *              package: com.example.super_awesome
 *              pluginClass: SuperAwesomePlugin
 *          ios:
 *              pluginClass: SuperAwesomePlugin
 */
internal data class PubspecFlutter(
    @JsonProperty("plugin")
    internal val plugin: PubspecPlugin?,
)

/**
 *  plugin:
 *      platforms:
 *          android:
 *              package: com.example.super_awesome
 *              pluginClass: SuperAwesomePlugin
 *          ios:
 *              pluginClass: SuperAwesomePlugin
 */
internal data class PubspecPlugin(
    @JsonProperty("platforms")
    internal val platforms: PubspecPluginPlatforms?,
)

/**
 *   platforms:
 *      android:
 *         package: com.example.super_awesome
 *         pluginClass: SuperAwesomePlugin
 *      ios:
 *         pluginClass: SuperAwesomePlugin
 */
internal data class PubspecPluginPlatforms(
    @JsonProperty("android")
    internal val android: PubspecPluginClass?,

    @JsonProperty("ios")
    internal val ios: PubspecPluginClass?,
)

/**
 *      android:
 *         package: com.example.super_awesome
 *         pluginClass: SuperAwesomePlugin
 *      ios:
 *         pluginClass: SuperAwesomePlugin
 */
internal data class PubspecPluginClass(
    @JsonProperty("package")
    internal val pluginPackage: String?,

    @JsonProperty("pluginClass")
    internal val pluginClass: String?,
)