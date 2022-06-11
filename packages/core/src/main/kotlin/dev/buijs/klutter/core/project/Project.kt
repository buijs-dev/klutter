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

package dev.buijs.klutter.core.project

import dev.buijs.klutter.core.verifyExists
import java.io.File

/**
 * A representation of the structure of a project made with the Klutter Framework.
 * Each property of this object represents a folder containing one or more folders
 * and/or files wich are in some way used or needed by Klutter.
 *
 * @property root is the top level of the project.
 * @property ios is the folder containing the iOS frontend code, basically the iOS folder from a standard Flutter project.
 * @property android is the folder containing the Android frontend code, basically the iOS folder from a standard Flutter project.
 * @property platform is the folder containing the native backend code, basically a Kotlin Multiplatform library module.
 * @author Gillian Buijs
 */
data class Project(
    val root: Root,
    val ios: IOS,
    val android: Android,
    val platform: Platform,
)

fun String.plugin(pluginName: String) =
    File(this).plugin(pluginName)

fun String.plugin() =
    File(this).plugin()

fun File.plugin(pluginName: String) =
    build(Root(pluginName, this))

fun File.plugin() = File("${absolutePath}/pubspec.yaml")
    .verifyExists()
    .let { Root(it.toPubspec().name,this) }
    .plugin()

fun Root.plugin() =
    build(this)

private fun build(root: Root): Project {
    val pubspec = root.toPubspec()
    return Project(
        root = root,
        ios = IOS(
            folder = root.resolve("ios"),
            pluginName = root.pluginName,
            pluginClassName = pubspec.ios?.pluginClass ?: root.pluginClassName
        ),
        platform = Platform(
            folder = root.resolve("platform"),
            pluginName = root.pluginName,
        ),
        android = Android(
            folder = root.resolve("android"),
            pluginPackageName = pubspec.android?.pluginPackage ?: "",
            pluginClassName = pubspec.android?.pluginClass ?: root.pluginClassName,
        ),
    )
}