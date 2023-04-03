@file:JvmName("PubspecBuilder")
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

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import dev.buijs.klutter.kore.KlutterException
import dev.buijs.klutter.kore.shared.verifyExists
import java.io.File

/**
 * The version of the klutter Pub Plugin.
 */
const val klutterPubVersion = "0.3.0"

/**
 * The version of the squint_json Pub Plugin.
 */
const val squintPubVersion = "0.0.5"

/**
 * Mapper to read/write YAML.
 */
val mapper = YAMLFactory.builder()
    .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
    .build()
    .let { factory ->
        ObjectMapper(factory)
            .also {
                it.registerKotlinModule()
                it.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
                it.setVisibility(it.serializationConfig.defaultVisibilityChecker
                        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                        .withGetterVisibility(JsonAutoDetect.Visibility.ANY)
                        .withSetterVisibility(JsonAutoDetect.Visibility.ANY)
                        .withCreatorVisibility(JsonAutoDetect.Visibility.ANY))
            }
    }

fun Root.toPubspec(): Pubspec =
    folder.resolve("pubspec.yaml").toPubspec()

fun File.toPubspec(): Pubspec {
    try {
        return mapper.readValue(verifyExists().readText(), Pubspec::class.java)
    } catch(e: Exception) {
        throw KlutterException("Failed to parse pubspec.yaml: ${e.message}")
    }
}

fun File.pubspecInit(
    name: String,
    androidPackageName: String,
    pluginClassName: String,
) = resolve("pubspec.yaml").also { yamlFile ->
    val pubspec = newProjectYaml(
        name = name,
        androidPackageName = androidPackageName,
        pluginClassName = pluginClassName
    )

    val yamlContent = pubspec.serialize()
    yamlFile.writeText(yamlContent)
}

/**
 * Output the [Pubspec] as valid pubspec.yaml content.
 */
fun Pubspec.serialize(): String =
    mapper.writeValueAsString(this)
        .replace("\"","")
        .replace("-", " ")

/**
 * Create a new [Pubspec] instance which can be serialized for
 * a new Klutter project.
 *
 * Example of serialized output:
 * ```
 * name: hello
 * description: A new klutter plugin project.
 * version: 0.0.1
 *
 * environment:
 *   sdk: '>=2.18.0 <3.0.0'
 *   flutter: ">=2.5.0"
 *
 * dependencies:
 *   flutter:
 *     sdk: flutter
 *   klutter: ^0.3.0
 *   squint_json: ^0.0.5
 *
 * flutter:
 *   plugin:
 *     platforms:
 *       android:
 *         package: com.example.hello
 *         pluginClass: HelloPlugin
 *       ios:
 *         pluginClass: HelloPlugin
 * ```
 */
fun newProjectYaml(
    name: String,
    androidPackageName: String,
    pluginClassName: String,
) = Pubspec(
    name = name,
    description = "A new klutter plugin project.",
    version = "0.0.1",
    environment = PubspecEnvironment(
        sdk = "'>=2.16.1 <3.0.0'",
        flutter = "'>=2.5.0'",
    ),
    dependencies = PubspecDependencies(
        dependencies = listOf(
            PubspecDependency(key = "klutter", valueOrSubKey = "^$klutterPubVersion"),
            PubspecDependency(key = "squint_json", valueOrSubKey = "^$squintPubVersion"),
            PubspecDependency(key = "flutter", valueOrSubKey = "sdk", nestedValue = "flutter")
        ),
    ),
    flutter = PubspecFlutter(
        plugin = PubspecPlugin(
            platforms = PubspecPluginPlatforms(
                android = PubspecPluginClass(
                    pluginPackage = androidPackageName,
                    pluginClass = pluginClassName,
                ),
                ios = PubspecPluginClass(
                    pluginClass = pluginClassName,
                ),
            )
        )
    )
)

internal object PubspecDependenciesDeserializer: JsonDeserializer<PubspecDependencies>() {
    override fun deserialize(
        parser: JsonParser,
        context: DeserializationContext,
    ): PubspecDependencies {
        val parent = context.readTree(parser)
        val dependencies = mutableListOf<PubspecDependency>()
        parent.fieldNames().forEach { name ->
            val node = parent.get(name)
            val dependency = node.toPubspecDependency(name)
            dependencies.add(dependency)
        }
        return PubspecDependencies(dependencies = dependencies.toList())
    }
}

internal object PubspecDependenciesSerializer: JsonSerializer<PubspecDependencies>() {
    override fun serialize(
        value: PubspecDependencies?,
        generator: JsonGenerator,
        serializers: SerializerProvider?
    ) {

        value?.dependencies?.let { dependencies ->
            if(dependencies.isNotEmpty()) {
                val sorted = dependencies.toMutableList()

                val flutterSdk =
                    dependencies.firstOrNull { it.key == "flutter" }

                if(flutterSdk != null) {
                    sorted.remove(flutterSdk)
                }

                generator.writeStartArray()

                sorted.sortedByDescending { it.valueOrSubKey }
                    .toMutableList()
                    .also { flutterSdk?.let { sdk -> it.add(sdk) } }
                    .reversed()
                    .forEach { dependency ->
                        PubspecDependencySerializer.serialize(
                            value = dependency,
                            generator = generator,
                            serializers = serializers
                        )
                    }

                generator.writeEndArray()
            }
        }
    }

}

internal object PubspecDependencySerializer: JsonSerializer<PubspecDependency>() {
    override fun serialize(
        value: PubspecDependency?,
        generator: JsonGenerator,
        serializers: SerializerProvider?
    ) {

        value?.let { dependency ->
            if(dependency.nestedValue != null) {
                generator.writeString("${dependency.key}:")
                generator.writeString("     ${dependency.valueOrSubKey}: ${dependency.nestedValue}")
            } else {
                generator.writeString("${dependency.key}: ${dependency.valueOrSubKey}")
            }
        }
    }

}

private fun JsonNode.toPubspecDependency(name: String): PubspecDependency {
    val version = this.textValue()

    if(version != null) {
        return PubspecDependency(key = name, valueOrSubKey = version)
    }

    val fieldName =
        this.fieldNames().next()

    val value =
        this.get(fieldName).textValue()

    return PubspecDependency(
        key = name,
        nestedValue = value,
        valueOrSubKey = fieldName
    )
}

fun Pubspec.iosClassName(orElse: String): String {
    return when {
        ios == null -> {
            orElse
        }
        ios!!.pluginClass == null -> {
            orElse
        }
        else -> {
            ios!!.pluginClass!!
        }
    }
}

fun Pubspec.androidClassName(orElse: String): String {
    return when {
        android == null -> {
            orElse
        }
        android!!.pluginClass == null -> {
            orElse
        }
        else -> {
            android!!.pluginClass!!
        }
    }
}

fun Pubspec.androidPackageName(): String {
    return when {
        android == null -> {
            ""
        }
        android!!.pluginPackage == null -> {
            ""
        }
        else -> {
            android!!.pluginPackage!!
        }
    }
}