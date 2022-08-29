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
import dev.buijs.klutter.tasks.GenerateApplicationProjectTask
import dev.buijs.klutter.tasks.GeneratePluginProjectTask
import org.jetbrains.plugins.gradle.autolink.GradleUnlinkedProjectAware
import java.io.File

object KlutterTaskFactory {
    fun build(
        pathToRoot: String,
        config: KlutterTaskConfig,
        project: Project? = null,
    ): Task = when(config.projectType) {
        KlutterProjectType.PLUGIN -> {
            createKlutterPluginTask(
                pathToRoot = pathToRoot,
                project = project,
                name = config.appName ?: klutterPluginDefaultName,
                group = config.groupName ?: klutterPluginDefaultGroup,
            )
        }
        KlutterProjectType.APPLICATION -> {
            createKlutterApplicationTask(
                pathToRoot = pathToRoot,
                project = project,
                name = config.appName,
                group = config.groupName,
            )
        }
    }
}

internal fun createKlutterPluginTask(
    pathToRoot: String,
    name: String,
    group: String,
    project: Project? = null,
) = createKlutterTask(
    pathToRoot = pathToRoot,
    project = project,
    task = {
        GeneratePluginProjectTask(
            pathToRoot = pathToRoot,
            pluginName = name,
            groupName = group,
        ).run().also {

            println("Running GeneratePluginProjectTask")

            File(pathToRoot).let { root ->
                root.resolve(name).let { subRoot ->
                    // Change subRoot name just in case root and subRoot name are identicial.
                    File("${subRoot.parent}/klutterTempFolderName").let { temp ->
                        subRoot.renameTo(temp)
                        temp.copyRecursively(root, overwrite = true)
                        temp.deleteRecursively()
                    }

                    // Adjust the path in the .klutter-plugins file because it is
                    // an absolute path which is now incorrect due to moving the entire
                    // generated folder to a parent folder.
                    root.resolve("example/.klutter-plugins")
                        .writeText(":klutter:$name=${root.absolutePath}/android/klutter")
                }

                root.resolve("gradlew.bat").let { gradlew ->
                    gradlew.setExecutable(true, false)
                    gradlew.setReadable(true, false)
                    gradlew.setWritable(true, false)
                }

                root.resolve("gradlew").let { gradlew ->
                    gradlew.setExecutable(true, false)
                    gradlew.setReadable(true, false)
                    gradlew.setWritable(true, false)
                }

            }

        }
    }
)

internal fun createKlutterApplicationTask(
    pathToRoot: String,
    name: String? = null,
    group: String? = null,
    project: Project? = null,
) = createKlutterTask(
    pathToRoot = pathToRoot,
    project = project,
    task = {
        GenerateApplicationProjectTask(
            pathToRoot = pathToRoot,
            appName = name,
            groupName = group,
        ).run()
    }
)

@Suppress("DialogTitleCapitalization")
private fun createKlutterTask(
    project: Project? = null,
    pathToRoot: String,
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
                .linkAndLoadProject(it, pathToRoot)
        }
        progressIndicator.fraction = 1.0
    }
}