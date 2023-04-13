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

@file:Suppress("MemberVisibilityCanBePrivate")

package dev.buijs.klutter.kore.project

import dev.buijs.klutter.kore.KlutterException
import dev.buijs.klutter.kore.shared.verifyExists
import java.io.File

/**
 * Wrapper class with a path to the [root]/ios.
 *
 * @property folder path to the iOS folder.
 */
class IOS(
    val folder: File,
    val pluginName: String,
    val pluginClassName: String,
) {

    val pathToClasses: File = folder.resolve("Classes")
    val pathToPlugin: File = pathToClasses.resolve("$pluginClassName.swift")

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