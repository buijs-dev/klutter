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

package dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter


import dev.buijs.klutter.core.KlutterCodeGenerationException
import dev.buijs.klutter.core.KlutterLogger
import dev.buijs.klutter.core.KlutterVisitor
import java.io.File


/**
 * Class which may or may not edit the Info.plist file in flutter/ios/Runner folder.
 *
 * May update the keys <i>CFBundleDisplayName</i> and <i>CFBundleName</i>
 * to be identical to the app name specified in the Klutter Plugin.
 *
 * @author Gillian Buijs
 */
internal class IosInfoPlistVisitor(
    private val infoPlist: File,
    private val appName: String
): KlutterVisitor {

    override fun visit(): KlutterLogger {
        if(!infoPlist.exists()) {
            throw KlutterCodeGenerationException("Could not locate Info.plist file at path: ${infoPlist.absolutePath}")
        }

        val logger = KlutterLogger()
        val lines = infoPlist.readLines()
        val output = mutableListOf<String>()
        var setDisplayName = false
        var setName = false

        for (line in lines) {
            when {

                line.contains("<key>CFBundleDisplayName</key>") -> {
                    setDisplayName = true
                    output.add(line)
                }

                line.contains("<key>CFBundleName</key>") -> {
                    setName = true
                    output.add(line)
                }

                setDisplayName -> {
                    output.add("\t\t<string>${appName.toDisplayName()}</string>")
                    setDisplayName = false
                }

                setName -> {
                    output.add("\t\t<string>${appName.toName()}</string>")
                    setName = false
                }

                else -> output.add(line)
            }

        }

        infoPlist.delete()
        infoPlist.createNewFile()
        infoPlist.writeText(output.joinToString("\r\n"))

        return logger
    }

    private fun String.toDisplayName() = this
        .trim()
        .replace("_", " ")
        .replace(" ", "_")
        .split("_")
        .joinToString(" ") {
            it.replaceFirstChar { char -> char.uppercase()
            }
        }

    private fun String.toName() = this
        .trim()
        .replace("_", " ")
        .replace(" ", "_")
        .lowercase()


}