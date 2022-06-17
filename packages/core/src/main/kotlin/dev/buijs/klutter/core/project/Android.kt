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

import dev.buijs.klutter.core.KlutterException
import dev.buijs.klutter.core.shared.verifyExists
import java.io.File

/**
 * Wrapper class with a file instance pointing to the android sub-module.
 *
 * @property folder path to the Android folder.
 */
class Android(
    val folder: File,
    val pluginPackageName: String,
    val pluginClassName: String,
) {

    /**
     * Return path to android/src/main/AndroidManifest.xml.
     *
     * @throws KlutterException if path/file does not exist.
     * @return [File] AndroidManifest.xml.
     */
    val manifest = folder.verifyExists()
        .resolve("src/main")
        .verifyExists()
        .resolve("AndroidManifest.xml")
        .verifyExists()

    val pathToPlugin: File = folder
        .resolve("src/main/kotlin/${pluginPackageName.toPath()}")
        .verifyExists()
        .resolve("$pluginClassName.kt")

    private fun String?.toPath(): String =
        this?.replace(".", "/") ?: ""
}