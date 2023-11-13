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
package dev.buijs.klutter.kradle.wizard

import dev.buijs.klutter.kore.common.EitherNok
import dev.buijs.klutter.kore.common.EitherOk
import dev.buijs.klutter.kore.common.ExcludeFromJacocoGeneratedReport
import dev.buijs.klutter.kore.project.*
import dev.buijs.klutter.kore.tasks.project.*
import dev.buijs.klutter.kradle.shared.NewProjectInput
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

private const val groupMessage = "Enter Groupname (organisation):"

private const val groupDefault = "com.example"

private const val nameMessage = "Enter Plugin name:"

private const val nameDefault = "my_plugin"

private const val createProjectInCurrentFolderMessage = "Create project in current folder?"

private const val createProjectInCurrentFolderDefault = true

private const val projectFolderPathMessage = "Enter path where to create the project:"

private const val projectFolderPathDefault = ""

@ExcludeFromJacocoGeneratedReport
internal fun getNewProjectOptionsByUserInput(): ProjectBuilderOptions =
    NewProjectWizard().toProjectBuilderOptions()

internal fun ProjectBuilderOptions.confirmNewProjectOptions(): ProjectBuilderOptions? {
    println("  Confirm project details")
    println("  - Plugin Name: ${pluginName.validPluginNameOrThrow()}")
    println("  - Group Name: ${groupName.validPluginNameOrThrow()}")
    println("  - Flutter: $flutterDistributionString")
    println("  - Dependencies:")
    println("       - klutter: ${config?.dependencies?.klutter ?: klutterPubVersion}")
    println("       - klutter_ui: ${config?.dependencies?.klutterUI ?: klutterUIPubVersion}")
    println("       - squint_json: ${config?.dependencies?.squint ?: squintPubVersion}")
    println("       - bom: ${config?.bomVersion ?: klutterBomVersion}")
    println("")
    val confirmed = mrWizard.promptConfirm(message = "Create project?", default = true)
    return if(confirmed) this else null
}

internal class NewProjectWizard(
    override val rootFolder: RootFolder =
        RootFolderQuestion().ask(),

    override val groupName: GroupName =
        toGroupName(askForGroupName()),

    override val pluginName: PluginName =
        toPluginName(askForPluginName()),

    private val prettyPrintedFlutterDistribution: String =
        askForFlutterVersion(),

    override val configOrNull: Config? =
        askForConfig(),

    ) : NewProjectInput {
    override val flutterDistributionFolderName: FlutterDistributionFolderName
        get() = PrettyPrintedFlutterDistribution(prettyPrintedFlutterDistribution).flutterDistribution.folderNameString
}

private fun NewProjectWizard.toProjectBuilderOptions(): ProjectBuilderOptions =
    ProjectBuilderOptions(
        rootFolder = rootFolder,
        groupName = groupName,
        pluginName = pluginName,
        flutterDistributionString = flutterDistributionFolderName,
        config = configOrNull)

private fun askForGroupName(): String =
    mrWizard.promptInput(message = groupMessage, default = groupDefault)

private fun askForPluginName(): String =
    mrWizard.promptInput(message = nameMessage, default = nameDefault)

private class RootFolderQuestion {
    private var rootFolder: RootFolder? = null

    fun ask(): RootFolder {
        rootFolder = toRootFolder(askForRootFolder())
        if(rootFolder is EitherOk)
            return rootFolder!!

        if(rootFolder is EitherNok)
            println((rootFolder as EitherNok<String, File>).data)

        return ask()
    }
}

private fun askForRootFolder(): String {
    val useCurrentFolder = mrWizard.promptConfirm(
        message = createProjectInCurrentFolderMessage,
        default = createProjectInCurrentFolderDefault
    )

    if(useCurrentFolder)
        return Path.of("").absolutePathString()

    return mrWizard.promptInput(
        message = projectFolderPathMessage,
        default = Paths.get(projectFolderPathDefault).absolutePathString())
}

private fun askForConfig(): Config? {
    val useConfig = mrWizard.promptConfirm(
        message = "Configure dependencies?",
        default = false)

    if(!useConfig) return null

    val klutter = askForSource(
        name = "klutter",
        stableVersion = klutterPubVersion,
        gitUrl = "https://github.com/buijs-dev/klutter-dart.git@develop")

    val klutterUI = askForSource(
        name = "klutter_ui",
        stableVersion = klutterUIPubVersion,
        gitUrl = "https://github.com/buijs-dev/klutter-dart-ui.git@develop")

    val squint = askForSource(
        name = "squint_json",
        stableVersion = squintPubVersion,
        gitUrl = "https://github.com/buijs-dev/squint.git@develop")

    val bomVersion = askForKlutterGradleBomVersion()

    return Config(
        bomVersion = bomVersion,
        dependencies = Dependencies(
            klutter = klutter,
            klutterUI = klutterUI,
            squint = squint))
}

internal fun askForFlutterVersion(): String =
    mrWizard.promptList(
        hint = "press Enter to pick",
        message = "Select Flutter SDK version:",
        choices = flutterVersionsDescending(currentOperatingSystem).map { "${it.prettyPrintedString}" })

private fun askForSource(name: String, stableVersion: String, gitUrl: String): String {
    val git = "Git@Develop"
    val pub = "Pub@^$stableVersion"
    val chosen = mrWizard.promptList(
        hint = "press Enter to pick",
        message = "Get $name source from:",
        choices = listOf(git, pub, "Local"))
    return when {
        chosen.startsWith(git) -> gitUrl
        chosen.startsWith("Local") -> "local@${askForPathToLocal(name = name)}"
        else -> stableVersion
    }
}

private fun askForPathToLocal(name: String): String = mrWizard.promptInput(
    message = "Enter path to $name (dart) library:",
    default = Paths.get("").absolutePathString())

private fun askForKlutterGradleBomVersion(): String = mrWizard.promptInput(
    message = "Enter bill-of-materials version:",
    default = klutterBomVersion)