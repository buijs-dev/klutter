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
package dev.buijs.klutter.kradle.shared

import dev.buijs.klutter.kore.KlutterException
import dev.buijs.klutter.kore.project.*
import dev.buijs.klutter.kore.tasks.finish
import java.io.File

internal fun List<String>.execFlutterCommand(currentFolder: File): String =
    toTypedArray().execFlutterCommand(currentFolder)

internal fun Array<String>.execFlutterCommand(currentFolder: File): String {
    val flutter = findFlutterDistributionOrThrow(currentFolder)
    return this.let { args ->
        try {
            ProcessBuilder()
                .command(buildList {
                    if(isWindows) {
                        add("cmd.exe")
                        add("/c")
                    }

                    add(flutter)
                    addAll(args)
                })
                .directory(currentFolder)
                .inheritIO()
                .start()
                .finish(30L)
        } catch(e: Exception) {
            "oops..."
        }
    }
}

private fun findFlutterDistributionOrThrow(currentFolder: File): String {
    val version = currentFolder
        .resolve("kradle.yaml")
        .resolveProjectPropertiesOrThrow()
        .resolveSystemPropertiesOrThrow()
        .readText()
        .let { findFlutterVersionInKradleYamlOrNull(it) }
        ?.let { version ->
            version.split(".").let { parts ->
                Version(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
            }
        } ?: throw KlutterException("Failed to determine Flutter version from kradle.yaml")

    val distribution = FlutterDistributionFolderName("${version.prettyPrint}.${currentOperatingSystem.value}.${currentArchitecture.name}")
    return flutterExecutable(distribution).absolutePath
}