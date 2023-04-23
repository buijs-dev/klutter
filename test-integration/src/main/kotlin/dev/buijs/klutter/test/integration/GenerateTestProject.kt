package dev.buijs.klutter.test.integration

import dev.buijs.klutter.kore.project.Project
import dev.buijs.klutter.kore.project.plugin
import dev.buijs.klutter.tasks.input.toGroupName
import dev.buijs.klutter.tasks.input.toPluginName
import dev.buijs.klutter.tasks.input.toRootFolder
import dev.buijs.klutter.tasks.project.ProjectBuilderOptions
import dev.buijs.klutter.tasks.project.ProjectBuilderTask
import java.nio.file.Files.createTempDirectory
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectory
import kotlin.io.path.createFile
import kotlin.io.path.writeText

fun generatePlugin(): Project {

    val root = createTempDirectory("")
        .also {
            it.resolve("my_custom_plugin")
                .also { dir -> dir.createDirectory() }
                .resolve("klutter.yaml")
                .also { yaml -> yaml.createFile() }
                .also { yaml -> yaml.writeText("""
                    |dependencies:
                    |    klutter: 'https://github.com/buijs-dev/klutter-dart.git@develop'
                    |    klutter_ui: 'https://github.com/buijs-dev/klutter-dart-ui.git@develop'
                    |    squint_json: 'https://github.com/buijs-dev/squint.git@develop'
                """.trimMargin()) }
    }

    ProjectBuilderTask(
        ProjectBuilderOptions(
            rootFolder = toRootFolder(root.absolutePathString()),
            pluginName = toPluginName("my_custom_plugin"),
            groupName = toGroupName("com.example.apps")))
        .run()

    return root.resolve("my_custom_plugin").toFile().plugin()

}