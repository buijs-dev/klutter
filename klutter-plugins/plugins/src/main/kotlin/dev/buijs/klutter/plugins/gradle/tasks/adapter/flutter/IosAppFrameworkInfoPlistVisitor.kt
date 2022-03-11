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
 * Class which may or may not edit the AppFrameworkInfo.plist file in flutter/ios/Flutter folder.
 *
 * May update the key <i>MinimumOSVersion</i> to be identical to the version specified in the Klutter Plugin.
 *
 * @author Gillian Buijs
 */
internal class IosAppFrameworkInfoPlistVisitor(
    private val appFrameworkInfo: File,
    private val iosVersion: String
): KlutterVisitor {

    override fun visit(): KlutterLogger {
        if(!appFrameworkInfo.exists()) {
            throw KlutterCodeGenerationException("Could not locate AppFrameworkInfo.plist file at path: ${appFrameworkInfo.absolutePath}")
        }

        val logger = KlutterLogger()
        val rawContent = appFrameworkInfo.readText().filter { !it.isWhitespace() }

        if(rawContent.contains("<key>MinimumOSVersion</key><string>$iosVersion</string>")){
            logger.debug("AppFrameworkInfo.plist file is OK, no editing done.")
            return logger
        }

        val lines = appFrameworkInfo.readLines()
        val output = mutableListOf<String>()
        var setVersion = false
        for (line in lines) {
            when {

                line.contains("<key>MinimumOSVersion</key>") -> {
                    setVersion = true
                    output.add(line)
                }

                setVersion -> {
                    output.add("  <string>$iosVersion</string>")
                    setVersion = false
                }

                else -> output.add(line)
            }

        }

        appFrameworkInfo.delete()
        appFrameworkInfo.createNewFile()
        appFrameworkInfo.writeText(output.joinToString("\r\n"))

        return logger
    }
}

