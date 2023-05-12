/* Copyright (c) 2021 - 2023 Buijs Software
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
package dev.buijs.klutter.gradle.dsl

import dev.buijs.klutter.kore.KlutterException
import java.util.*

internal object KlutterVersion {
    private val properties: Properties = Properties().also {
        it.load(KlutterDependencyHandler::class.java.classLoader.getResourceAsStream("publish.properties"))
    }

    @JvmStatic
    val annotations: String = getOrThrow("annotations.version")

    @JvmStatic
    val compiler: String = getOrThrow("compiler.version")

    @JvmStatic
    val kore: String = getOrThrow("kore.version")

    @JvmStatic
    val gradle: String = getOrThrow("plugin.gradle.version")

    @JvmStatic
    val tasks: String = getOrThrow("tasks.version")

    @JvmStatic
    val kompose: String = getOrThrow("kompose.version")

    @JvmStatic
    val flutterEngine: String = getOrThrow("flutter.engine.version")

    @JvmStatic
    internal fun byName(simpleModuleName: String): String = when(simpleModuleName) {
        "annotations" -> annotations
        "kompose" -> kompose
        "kore" -> kore
        "gradle" -> gradle
        "tasks" -> tasks
        "compiler" -> compiler
        "flutter-engine-android" -> flutterEngine
        "flutter-engine-kmp-android" -> flutterEngine
        else -> throw KlutterException("Unknown module name '$simpleModuleName'.")
    }

    @JvmStatic
    internal fun getOrThrow(property: String): String = properties.getProperty(property)
        ?: throw KlutterException("Missing '$property' in Klutter Gradle Jar.")
}