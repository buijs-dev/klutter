package dev.buijs.klutter.test.integration

import dev.buijs.klutter.kore.project.Dependencies
import dev.buijs.klutter.kore.project.Config
import dev.buijs.klutter.kore.project.Project
import dev.buijs.klutter.kore.project.plugin
import dev.buijs.klutter.tasks.project.toGroupName
import dev.buijs.klutter.tasks.project.toPluginName
import dev.buijs.klutter.tasks.project.toRootFolder
import dev.buijs.klutter.tasks.project.ProjectBuilderOptions
import dev.buijs.klutter.tasks.project.ProjectBuilderTask
import java.nio.file.Files.createTempDirectory
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectory

fun generatePlugin(): Project {
    val root = createTempDirectory("").also {
        it.resolve("my_custom_plugin")
            .also { dir -> dir.createDirectory() } }

    ProjectBuilderTask(
        ProjectBuilderOptions(
            rootFolder = toRootFolder(root.absolutePathString()),
            pluginName = toPluginName("my_custom_plugin"),
            groupName = toGroupName("com.example.apps"),
            config = Config(
                dependencies = Dependencies(
                    klutter = "https://github.com/buijs-dev/klutter-dart.git@develop",
                    klutterUI = "https://github.com/buijs-dev/klutter-dart-ui.git@develop",
                    squint = "https://github.com/buijs-dev/squint.git@develop"))))
        .run()
    return root.resolve("my_custom_plugin").toFile().plugin()
}