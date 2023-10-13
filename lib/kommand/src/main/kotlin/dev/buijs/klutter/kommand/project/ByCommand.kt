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
package dev.buijs.klutter.kommand.project

import dev.buijs.klutter.kommand.flutterw.downloadFlutter
import dev.buijs.klutter.kore.KlutterException
import dev.buijs.klutter.kore.project.*
import dev.buijs.klutter.tasks.project.*
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import java.io.File

private const val rootDescription = "Path to root folder"

private const val groupDescription = "Groupname (organisation)"

private const val nameDescription = "Name of app (plugin)"

private const val configDescription = "Path to klutter.yaml"

private const val flutterDescription = "Flutter SDK version (format: major.minor.patch and if macos then add the architecture, example: 3.0.4.ARM64)"

internal fun List<String>.projectBuilderOptionsByCommand(): ProjectBuilderOptions =
    toTypedArray().projectBuilderOptionsByCommand()

internal fun Array<String>.projectBuilderOptionsByCommand(): ProjectBuilderOptions =
    ByCommand(parser = ArgParser("klutter"), args = this).toProjectBuilderOptions()

private fun ByCommand.toProjectBuilderOptions(): ProjectBuilderOptions =
    ProjectBuilderOptions(
        rootFolder = rootFolder,
        groupName = groupName,
        pluginName = pluginName,
        flutterVersion = flutterVersion,
        config = configOrNull)

private class ByCommand(parser: ArgParser, args: Array<String>): Input {

    private val root by parser required rootDescription

    private val name by parser required nameDescription

    private val group by parser required groupDescription

    private val flutter by parser required flutterDescription

    private val config by parser optional configDescription

    init {
        parser.parse(args)
    }

    override val rootFolder: RootFolder
        get() = toRootFolder(root)

    override val groupName: GroupName
        get() = toGroupName(group)

    override val pluginName: PluginName
        get() = toPluginName(name)

    override val flutterVersion: String
        get() {
            val splitted = flutter.trim().uppercase().split(".")
            val major = splitted.getOrNull(0)?.toIntOrNull()
                ?: throw KlutterException("Flutter version is invalid: $flutter")
            val minor = splitted.getOrNull(1)?.toIntOrNull()
                ?: throw KlutterException("Flutter version is invalid: $flutter")
            val patch = splitted.getOrNull(2)?.toIntOrNull()
                ?: throw KlutterException("Flutter version is invalid: $flutter")
            val os = if(isWindows) OperatingSystem.WINDOWS else OperatingSystem.MACOS
            val arch = splitted.getOrNull(3)?.let { Architecture.valueOf(it) }
            if(arch == null && os == OperatingSystem.MACOS)
                throw KlutterException("Architecture (X64 or ARM64) is missing")
            val version = Flutter(
                version = Version(major, minor, patch),
                os = os,
                arch = arch ?: Architecture.X64)
            val sdkLocation = flutterSDK(version.folderName)
            if(!sdkLocation.exists())
                downloadFlutter(version)
            return version.folderName
        }

    override val configOrNull: Config?
        get() = config?.let { File(it).toConfigOrNull() }
}

private infix fun ArgParser.required(description: String) =
    option(ArgType.String, description = description).required()

private infix fun ArgParser.optional(description: String) =
    option(ArgType.String, description = description)