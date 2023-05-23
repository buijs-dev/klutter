/* Copyright (c) 2021 - 2022 Buijs Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package dev.buijs.klutter.kore.project

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.databind.*

/**
 * The parsed pubspec.yaml file.
 *
 * Example:
 *
 *  ```
 *  name: ridiculous_plugin
 *  description: A new flutter plugin project.
 *  version: 0.0.1
 *  homepage: www.my_homepage.com
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
@JsonPropertyOrder(
    "name",
    "description",
    "version",
    "environment",
    "dependencies",
    "dev_dependencies",
    "flutter")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Pubspec(

    @JsonProperty("name")
    val name: String? = null,

    @JsonProperty("description")
    val description: String? = null,

    @JsonProperty("version")
    val version: String? = null,

    @JsonProperty("flutter")
    val flutter: PubspecFlutter? = null,

) {

    /**
     * The plugin content.
     */
    val plugin: PubspecPlugin?
        @JsonIgnore
        get() = flutter?.plugin

    /**
     * The supported plugin platforms.
     *
     * In the context of Klutter this is only iOS and Android.
     */
    val platforms: PubspecPluginPlatforms?
        @JsonIgnore
        get() = plugin?.platforms

    /**
     * The Android plugin class name.
     */
    val android: PubspecPluginClass?
        @JsonIgnore
        get() = platforms?.android

    /**
     * The IOS plugin class name.
     */
    val ios: PubspecPluginClass?
        @JsonIgnore
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
@JsonIgnoreProperties(ignoreUnknown = true)
data class PubspecFlutter(
    @JsonProperty("plugin")
    val plugin: PubspecPlugin?,
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
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class PubspecPlugin(
    @JsonProperty("platforms")
    val platforms: PubspecPluginPlatforms?,
)

/**
 *   platforms:
 *      android:
 *         package: com.example.super_awesome
 *         pluginClass: SuperAwesomePlugin
 *      ios:
 *         pluginClass: SuperAwesomePlugin
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class PubspecPluginPlatforms(
    @JsonProperty("android")
    val android: PubspecPluginClass?,

    @JsonProperty("ios")
    val ios: PubspecPluginClass?,
)

/**
 *      android:
 *         package: com.example.super_awesome
 *         pluginClass: SuperAwesomePlugin
 *      ios:
 *         pluginClass: SuperAwesomePlugin
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class PubspecPluginClass(
    @JsonProperty("package")
    val pluginPackage: String? = null,

    @JsonProperty("pluginClass")
    val pluginClass: String?,
)