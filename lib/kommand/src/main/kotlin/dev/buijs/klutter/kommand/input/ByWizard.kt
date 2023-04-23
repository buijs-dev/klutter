package dev.buijs.klutter.kommand.input

import com.github.kinquirer.KInquirer
import com.github.kinquirer.components.*
import dev.buijs.klutter.kore.common.EitherNok
import dev.buijs.klutter.kore.common.EitherOk
import dev.buijs.klutter.tasks.input.*
import dev.buijs.klutter.tasks.project.ProjectBuilderOptions
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

internal fun getOptionsByUserInput(): ProjectBuilderOptions {

    val rootFolder = RootFolderQuestion().ask()

    val group = askForGroupName()

    val plugin = askForPluginName()

    return ProjectBuilderOptions(
        rootFolder = rootFolder,
        groupName = toGroupName(group),
        pluginName = toPluginName(plugin)
    )
}

private fun askForGroupName(): String =
    KInquirer.promptInput(message = "Enter organisation:", default = "com.example")

private fun askForPluginName(): String =
    KInquirer.promptInput(message = "Enter plugin-name:", default = "")

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