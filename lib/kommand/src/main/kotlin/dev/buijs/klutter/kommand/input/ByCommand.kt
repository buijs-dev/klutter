package dev.buijs.klutter.kommand.input

import dev.buijs.klutter.tasks.project.ProjectBuilderOptions
import dev.buijs.klutter.tasks.project.toGroupName
import dev.buijs.klutter.tasks.project.toPluginName
import dev.buijs.klutter.tasks.project.toRootFolder
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required

internal fun Array<String>.determineProjectBuilderOptionsByCommand(): ProjectBuilderOptions {
    val parser = ArgParser("klutter")
    val root by parser.option(ArgType.String, shortName = "root", description = "Path to root folder").required()
    val group by parser.option(ArgType.String, shortName = "org", description = "Organization").required()
    val name by parser.option(ArgType.String, shortName = "name", description = "Plugin name").required()
    parser.parse(this)
    return ProjectBuilderOptions(
        rootFolder = toRootFolder(root),
        groupName = toGroupName(group),
        pluginName = toPluginName(name)
    )
}