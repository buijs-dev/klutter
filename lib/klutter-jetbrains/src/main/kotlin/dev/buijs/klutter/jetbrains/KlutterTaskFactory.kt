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
package dev.buijs.klutter.jetbrains

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import dev.buijs.klutter.kore.KlutterException
import dev.buijs.klutter.tasks.GenerateKlutterApplicationProjectTask
import dev.buijs.klutter.tasks.GenerateKlutterPluginProjectTask
import org.jetbrains.plugins.gradle.autolink.GradleUnlinkedProjectAware
import java.io.File

internal object KlutterTaskFactory {
    fun build(
        project: Project,
        pathToRoot: String,
        config: KlutterTaskConfig,
    ): Task = when(config.projectType) {
        KlutterProjectType.PLUGIN -> {
            createKlutterPluginTask(
                project = project,
                pathToRoot = pathToRoot,
                name = config.appName ?: klutterPluginDefaultName,
                group = config.groupName ?: klutterPluginDefaultGroup,
            )
        }
        KlutterProjectType.APPLICATION -> {
            createKlutterApplicationTask(
                project = project,
                pathToRoot = pathToRoot,
                name = config.appName,
                group = config.groupName,
            )
        }
    }
}

internal fun createKlutterPluginTask(
    project: Project,
    pathToRoot: String,
    name: String,
    group: String,
) = createKlutterTask(
    pathToRoot = pathToRoot,
    project = project,
    task = {
        GenerateKlutterPluginProjectTask(
            pathToRoot = pathToRoot,
            appName = name,
            groupName = group,
        ).run().also {
            File(pathToRoot).let { root ->
                val subRoot = root.resolve(name)
                subRoot.copyRecursively(root)
                subRoot.deleteRecursively()
            }
        }
    }
)

internal fun createKlutterApplicationTask(
    project: Project,
    pathToRoot: String,
    name: String? = null,
    group: String? = null,
) = createKlutterTask(
    pathToRoot = pathToRoot,
    project = project,
    task = {
        GenerateKlutterApplicationProjectTask(
            pathToRoot = pathToRoot,
            appName = name,
            groupName = group,
        ).run()
    }
)

@Suppress("DialogTitleCapitalization")
private fun createKlutterTask(
    project: Project,
    pathToRoot: String,
    task: () -> Unit,
) = object: Task.Modal(project, "Initializing project", false) {
    override fun run(indicator: ProgressIndicator) {
        val progressIndicator =  ProgressManager.getInstance().progressIndicator
        progressIndicator.text = "Loading..."
        progressIndicator.fraction = 0.1
        progressIndicator.fraction = 0.2
        progressIndicator.fraction = 0.3
        task.invoke()
        progressIndicator.fraction = 0.8
        GradleUnlinkedProjectAware().linkAndLoadProject(project, pathToRoot)
        progressIndicator.fraction = 1.0
    }
}