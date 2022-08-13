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

import dev.buijs.klutter.kore.KlutterException
import dev.buijs.klutter.kore.shared.toCamelCase
import java.io.File

/**
 * @property folder path to the top level of the project.
 */
class Root(val pluginName: String, file: File) {

    val folder: File =
        if (!file.exists()) {
            throw KlutterException("The root folder does not exist: ${file.absolutePath}.")
        } else {
            file.absoluteFile
        }

    val pathToLib = resolve("lib/$pluginName.dart")

    val pluginClassName = pluginName
        .toCamelCase()
        .replaceFirstChar { it.uppercase() }

    fun resolve(to: String): File = folder.resolve(to).normalize().absoluteFile
}