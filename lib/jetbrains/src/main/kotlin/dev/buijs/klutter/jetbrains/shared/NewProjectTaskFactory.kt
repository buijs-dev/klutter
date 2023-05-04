/* Copyright (c) 2021 - 2022 Buijs Software
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
package dev.buijs.klutter.jetbrains.shared

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import dev.buijs.klutter.tasks.executor
import dev.buijs.klutter.tasks.project.*
import org.jetbrains.plugins.gradle.autolink.GradleUnlinkedProjectAware
import java.io.File

/**
 * Factory which returns the correct task to be executed based on the [KlutterProjectType].
 */
object NewProjectTaskFactory {
    fun build(
        pathToRoot: String,
        config: NewProjectConfig,
        project: Project? = null,
    ): Task = createKlutterPluginTask(
        project = project,
        options = ProjectBuilderOptions(
            rootFolder = toRootFolder(pathToRoot),
            pluginName = toPluginName(config.appName ?: klutterPluginDefaultName),
            groupName = toGroupName(config.groupName ?: klutterPluginDefaultGroup)
        ))
}

/**
 * Create a Klutter plugin project.
 */
private fun createKlutterPluginTask(
    options: ProjectBuilderOptions,
    project: Project? = null,
): Task.Modal {

    val task = ProjectBuilderTask(options = options)
    val rootFolder = options.rootFolder.validRootFolderOrThrow()
    return createKlutterTask(
        pathToRoot = rootFolder,
        project = project,
        task = {
            executor = JetbrainsExecutor()
            task.run()
            rootFolder.moveUpFolder(options.pluginName.validPluginNameOrThrow()) }

    )
}

/**
 * Copies the generated project to the root folder.
 */
private fun File.moveUpFolder(pluginName: String) {
    this.resolve(pluginName).let { subRoot ->
        // Change subRoot name just in case root and subRoot name are identicial.
        File("${subRoot.parent}/klutterTempFolderName").let { temp ->
            subRoot.renameTo(temp)
            temp.copyRecursively(this, overwrite = true)
            temp.deleteRecursively()
        }

        // Adjust the path in the .klutter-plugins file because it is
        // an absolute path which is now incorrect due to moving the entire
        // generated folder to a parent folder.
        resolve("example/.klutter-plugins")
            .writeText(":klutter:$pluginName=${absolutePath}/android/klutter")
        resolve("gradlew.bat").makeExecutable()
        resolve("gradlew").makeExecutable()
    }
}

/**
 * Make a file executable, readable and writable because why not.
 */
private fun File.makeExecutable() {
    this.setExecutable(true, false)
}

/**
 * Display a loading popup while generating a project.
 */
@Suppress("DialogTitleCapitalization")
private fun createKlutterTask(
    project: Project? = null,
    pathToRoot: File,
    task: () -> Unit,
) = object: Task.Modal(null,"Initializing project", false) {
    override fun run(indicator: ProgressIndicator) {
        val progressIndicator =  ProgressManager.getInstance().progressIndicator
        progressIndicator.isIndeterminate = false
        progressIndicator.text = "Loading..."
        progressIndicator.fraction = 0.1
        progressIndicator.fraction = 0.2
        progressIndicator.fraction = 0.3
        task.invoke()
        progressIndicator.fraction = 0.8
        project?.let {
            GradleUnlinkedProjectAware()
                .linkAndLoadProject(it, pathToRoot.absolutePath)
        }
        progressIndicator.fraction = 1.0
    }
}