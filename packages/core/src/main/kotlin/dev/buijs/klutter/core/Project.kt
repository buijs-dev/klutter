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

@file:Suppress("unused")
package dev.buijs.klutter.core

import dev.buijs.klutter.core.shared.PubspecVisitor
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
data class KlutterProject(
    val root: Root,
    val ios: IOS,
    val android: Android,
    val platform: Platform,
)

fun String.klutterProject(
    pluginName: String? = null,
) = Root(File(this)).let {
    build(it, pluginName ?: it.pluginNameFromYaml())
}

fun File.klutterProject(
    pluginName: String? = null,
) = Root(this).let {
    build(it, pluginName ?: it.pluginNameFromYaml())
}

fun Root.klutterProject(pluginName: String) =
    build(this, pluginName)

fun Root.klutterProject() =
    build(this, this.pluginNameFromYaml())

private fun build(
    root: Root,
    pluginName: String,
): KlutterProject {
    return KlutterProject(
        root = root,
        ios = IOS(root.resolve("ios"), pluginName),
        platform = Platform(root.resolve("platform"), pluginName),
        android = Android(root.resolve("android")),
    )
}

private fun Root.pluginNameFromYaml(): String =
    PubspecVisitor(folder.resolve("pubspec.yaml")).appName()

/**
 * @property folder path to the top level of the project.
 */
class Root(file: File) {

    val folder: File = if (!file.exists()) {
        throw KlutterException("""
              The root folder does not exist: ${file.absolutePath}.
              """.trimIndent())
    } else {
        file.absoluteFile
    }

    fun resolve(to: String): File = folder.resolve(to).normalize().absoluteFile
}

/**
 * Wrapper class with a file instance pointing to the kmp sub-module in root/platform.
 *
 * @property folder path to the Platform folder.
 */
class Platform(
    val folder: File,
    private val pluginName: String,
) {

    /**
     * Function to return the location of the src module containing the common/shared platform code.
     *
     * @throws KlutterException if file(s) do not exist.
     * @return the absolute path to the common source code.
     */
    fun source() = folder
        .verifyExists()
        .resolve("src/commonMain")
        .verifyExists()

    /**
     * Function to return the location of the podspec in the platform sub-module.
     *
     * @throws KlutterException if file(s) do not exist.
     * @return the absolute path to the ios Podfile.
     */
    fun podspec() = folder
        .verifyExists()
        .resolve("$pluginName.podspec")
        .verifyExists()
}

/**
 * Wrapper class with a path to the [root]/ios.
 *
 * @property folder path to the iOS folder.
 */
class IOS(
    val folder: File,
    private val pluginName: String,
) {

    /**
     * Function to return the location of the PodFile in the ios sub-module.
     * If no custom path is given, Klutter assumes the path to the iOS Podfile is root-project/ios/PodFile.
     *
     * @throws KlutterException if file(s) do not exist.
     * @return the absolute path to the ios Podfile.
     */
    fun podfile() = folder
        .verifyExists()
        .resolve("Podfile")
        .verifyExists()

    /**
     * Function to return the location of the podspec in the ios sub-module.
     *
     * @throws KlutterException if file(s) do not exist.
     * @return the absolute path to the ios Podfile.
     */
    fun podspec() = folder
        .verifyExists()
        .resolve("$pluginName.podspec")
        .verifyExists()

    /**
     * Function to return the location of the AppDelegate.swift file in the ios folder.
     *
     * @throws KlutterException if file(s) do not exist.
     * @return the absolute path to the ios AppDelegate.
     */
    fun appDelegate() = folder
        .verifyExists()
        .resolve("Runner")
        .verifyExists()
        .resolve("AppDelegate.swift")
        .verifyExists()

}

/**
 * Wrapper class with a file instance pointing to the android sub-module.
 *
 * @property folder path to the Android folder.
 */
class Android(val folder: File) {

    /**
     * Return path to android/src/main/AndroidManifest.xml.
     *
     * @throws KlutterException if path/file does not exist.
     * @return [File] AndroidManifest.xml.
     */
    fun manifest() = folder.verifyExists()
        .resolve("src/main")
        .verifyExists()
        .resolve("AndroidManifest.xml")
        .verifyExists()

}