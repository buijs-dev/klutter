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

package dev.buijs.klutter.core.shared

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

/**
 * @author Gillian Buijs
 */
internal class FlutterPubspecScanner(
    private val pubspec: File,
) {

    fun scan(): PubspecData {
        val mapper = ObjectMapper(YAMLFactory())
        mapper.registerKotlinModule()
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        return mapper.readValue(pubspec, PubspecData::class.java)
    }

}

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class PubspecData(

    @JsonProperty("name")
    val libraryName: String,

    @JsonProperty("flutter")
    internal val flutter: Flutter,

    ) {

    val pluginClassName: String = flutter.plugin.platforms.android.pluginClass
    val packageName: String? = flutter.plugin.platforms.android.pluginPackage
}

internal data class Flutter(
    @JsonProperty("plugin")
    internal val plugin: Plugin,
)

/**
 * flutter:
 * plugin:
 *  platforms:
 *      android:
 *          package: com.example.super_awesome
 *          pluginClass: SuperAwesomePlugin
 *      ios:
 *          pluginClass: SuperAwesomePlugin
 */
internal data class Plugin(
    @JsonProperty("platforms")
    internal val platforms: Platforms,
)

internal data class Platforms(
    @JsonProperty("android")
    internal val android: PluginClass,

    @JsonProperty("ios")
    internal val ios: PluginClass,
)

internal data class PluginClass(
    @JsonProperty("package")
    internal val pluginPackage: String?,

    @JsonProperty("pluginClass")
    internal val pluginClass: String,
)