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
package dev.buijs.klutter.kommand.flutterw

import dev.buijs.klutter.kore.KlutterException
import dev.buijs.klutter.kore.project.Architecture
import dev.buijs.klutter.kore.project.OperatingSystem
import dev.buijs.klutter.kore.project.Version
import dev.buijs.klutter.kore.project.isWindows
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required

private const val majorVersionDescription = "Flutter major version (in 3.10.6, 3 is major)"

private const val minorVersionDescription = "Flutter minor version (in 3.10.6, 10 is minor)"

private const val patchVersionDescription = "Flutter patch version (in 3.10.6, 6 is patch)"

private const val architectureDescription = "X64 (Windows/intel Mac OS) or ARM64 (Apple Silicon Mac OS)"

internal fun List<String>.downloadFlutterOrThrow() {
    toTypedArray().downloadFlutterOrThrow()
}

internal fun Array<String>.downloadFlutterOrThrow() {
    ByCommand(parser = ArgParser("klutter"), args = this).downloadOrFail()
}

private fun ByCommand.downloadOrFail() {
    downloadFlutter(
        os = os,
        arch = architecture,
        version = Version(
            major = majorVersion,
            minor = minorVersion ?: latestVersionPlaceholder,
            patch = patchVersion ?: latestVersionPlaceholder))
}

private class ByCommand(parser: ArgParser, args: Array<String>): Input {

    private val major by parser required majorVersionDescription

    private val minor by parser optional minorVersionDescription

    private val patch by parser optional patchVersionDescription

    private val arch by parser optional architectureDescription

    init {
        parser.parse(args)
    }

    override val majorVersion: Int
        get() = major.toIntOrNull() ?: throw KlutterException("Invalid major version (should be Int): $major")

    override val minorVersion: Int?
        get() = when {
            minor == null -> null
            else -> minor!!.toIntOrNull() ?: throw KlutterException("Invalid minor version (should be Int): $minor")
        }

    override val patchVersion: Int?
        get() = when {
            patch == null -> null
            else -> patch!!.toIntOrNull() ?: throw KlutterException("Invalid patch version (should be Int): $minor")
        }

    override val architecture: Architecture
        get() {
            val trimmedAndScreaming = arch?.trim()?.uppercase() ?: ""
            return when  {
                trimmedAndScreaming == "X64" -> Architecture.X64
                trimmedAndScreaming == "ARM64" -> Architecture.ARM64
                trimmedAndScreaming.isNotEmpty() -> throw KlutterException("Invalid architecture (should be X64 or ARM64): $arch")
                isWindows -> Architecture.X64
                else -> throw KlutterException("Specify architecture (X64 for Intel Mac OS or ARM64 for Apple Silicon Mac OS)")
            }
        }

    override val os: OperatingSystem
        get() = if (isWindows) OperatingSystem.WINDOWS else OperatingSystem.MACOS

}

private infix fun ArgParser.required(description: String) =
    option(ArgType.String, description = description).required()

private infix fun ArgParser.optional(description: String) =
    option(ArgType.String, description = description)