package dev.buijs.klutter.kommand.input

import com.github.kinquirer.KInquirer
import com.github.kinquirer.components.*
import dev.buijs.klutter.kore.common.EitherNok
import dev.buijs.klutter.kore.common.EitherOk
import dev.buijs.klutter.kore.project.*
import dev.buijs.klutter.tasks.project.*
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

internal fun getOptionsByUserInput(): ProjectBuilderOptions {

    val rootFolder = RootFolderQuestion().ask()

    val group = askForGroupName()

    val plugin = askForPluginName()

    val config = askForConfig()

    return ProjectBuilderOptions(
        rootFolder = rootFolder,
        groupName = toGroupName(group),
        pluginName = toPluginName(plugin),
        config = config,
    )
}

private fun askForGroupName(): String =
    KInquirer.promptInput(message = "Enter organisation:", default = "com.example")

private fun askForPluginName(): String =
    KInquirer.promptInput(message = "Enter plugin-name:", default = "my_plugin")

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

private fun askForRootFolder(): String =
    if(KInquirer.promptConfirm(message = "Create project in current folder?", default = true)) {
        ""
    } else {
        KInquirer.promptInput(message = "Enter path where to create the project:", default = Paths.get("").absolutePathString())
    }

private fun askForConfig(): Config? =
    if(KInquirer.promptConfirm(message = "Configure dependencies?", default = false)) {
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
        Config(
            bomVersion = bomVersion,
            dependencies = Dependencies(
                klutter = klutter,
                klutterUI = klutterUI,
                squint = squint))
    } else null

private fun askForSource(name: String, stableVersion: String, gitUrl: String): String {
    val git = "Git@Develop"
    val pub = "Pub@^$stableVersion"
    val chosen = KInquirer.promptList(
        hint = "press Enter to pick",
        message = "Get $name source from:",
        choices = listOf(git, pub, "Local"))
    return when {
        chosen.startsWith(git) -> gitUrl
        chosen.startsWith("Local") -> "local@${askForPathToLocal(name = name)}"
        else -> stableVersion
    }
}

private fun askForPathToLocal(name: String): String = KInquirer.promptInput(
    message = "Enter path to $name (dart) library:",
    default = Paths.get("").absolutePathString())

private fun askForKlutterGradleBomVersion(): String = KInquirer.promptInput(
    message = "Enter bill-of-materials version:",
    default = klutterKommanderVersion)