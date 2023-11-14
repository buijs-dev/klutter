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
package dev.buijs.klutter.kradle.command

import dev.buijs.klutter.kore.project.*
import dev.buijs.klutter.kore.tasks.project.*
import dev.buijs.klutter.kradle.shared.NewProjectInput
import kotlinx.cli.ArgParser
import java.io.File

private const val rootDescription = "Path to root folder"

private const val groupDescription = "Groupname (organisation)"

private const val nameDescription = "Name of app (plugin)"

private const val configDescription = "Path to kradle.yaml"

private const val flutterDescription = "Flutter SDK version (format: major.minor.patch.platform.architecture, example: 3.10.6.macos.arm64)"

internal fun List<String>.getNewProjectOptions(): ProjectBuilderOptions =
    toTypedArray().getNewProjectOptions()

internal fun Array<String>.getNewProjectOptions(): ProjectBuilderOptions =
    NewProjectCommand(parser = ArgParser("klutter"), args = this).toProjectBuilderOptions()

private fun NewProjectCommand.toProjectBuilderOptions(): ProjectBuilderOptions =
    ProjectBuilderOptions(
        rootFolder = rootFolder,
        groupName = groupName,
        pluginName = pluginName,
        flutterDistributionString = flutterDistributionFolderName,
        config = configOrNull)

private class NewProjectCommand(parser: ArgParser, args: Array<String>): NewProjectInput {

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

    override val flutterDistributionFolderName: FlutterDistributionFolderName
        get() = FlutterDistributionFolderName(flutter)

    override val configOrNull: Config?
        get() = config?.let { File(it).toConfigOrNull() }

}