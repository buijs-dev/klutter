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
package dev.buijs.klutter.core.tasks.plugin.flutter

import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.KlutterPrinter
import dev.buijs.klutter.core.tasks.plugin.FlutterLibraryConfig
import dev.buijs.klutter.core.tasks.shared.DefaultWriter
import java.io.File

/**
 * @author Gillian Buijs
 */
internal class FlutterPubspecGenerator(
    private val path: File,
    private val config: FlutterLibraryConfig,
): KlutterFileGenerator() {

    override fun printer() = FlutterPubspecPrinter(
        libraryName = config.libraryName,
        libraryDescription = config.libraryDescription,
        libraryVersion = config.libraryVersion,
        libraryHomepage = config.libraryHomepage,
        developerOrganisation = config.developerOrganisation,
        pluginClassName = config.pluginClassName,
    )

    override fun writer() = DefaultWriter(path, printer().print())

}

internal class FlutterPubspecPrinter(
    private val libraryName: String,
    private val libraryDescription: String,
    private val libraryVersion: String,
    private val libraryHomepage: String,
    private val developerOrganisation: String,
    private val pluginClassName: String,
): KlutterPrinter {

    override fun print() = """
            |name: $libraryName
            |description: $libraryDescription
            |version: $libraryVersion
            |homepage: $libraryHomepage
            |
            |environment:
            |  sdk: ">=2.16.1 <3.0.0"
            |  flutter: ">=2.5.0"
            |
            |dependencies:
            |  flutter:
            |    sdk: flutter
            |
            |flutter:
            |  plugin:
            |    platforms:
            |      android:
            |        package: $developerOrganisation.$libraryName
            |        pluginClass: $pluginClassName
            |      ios:
            |        pluginClass: $pluginClassName
            |""".trimMargin()

}