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
package dev.buijs.klutter.kommand

import dev.buijs.klutter.kore.project.Config
import dev.buijs.klutter.kore.project.toConfigOrNull
import dev.buijs.klutter.tasks.project.*
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import java.io.File

private const val rootDescription = "Path to root folder"

private const val groupDescription = "Groupname (organisation)"

private const val nameDescription = "Name of app (plugin)"

private const val configDescription = "Path to klutter.yaml"

internal fun List<String>.projectBuilderOptionsByCommand(): ProjectBuilderOptions =
    toTypedArray().projectBuilderOptionsByCommand()

internal fun Array<String>.projectBuilderOptionsByCommand(): ProjectBuilderOptions =
    ByCommand(parser = ArgParser("klutter"), args = this).toProjectBuilderOptions()

private fun ByCommand.toProjectBuilderOptions(): ProjectBuilderOptions =
    ProjectBuilderOptions(
        rootFolder = rootFolder,
        groupName = groupName,
        pluginName = pluginName,
        config = configOrNull)

private class ByCommand(parser: ArgParser, args: Array<String>): Input {

    private val root by parser required rootDescription

    private val name by parser required nameDescription

    private val group by parser required groupDescription

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

    override val configOrNull: Config?
        get() = config?.let { File(it).toConfigOrNull() }
}

private infix fun ArgParser.required(description: String) =
    option(ArgType.String, description = description).required()

private infix fun ArgParser.optional(description: String) =
    option(ArgType.String, description = description)