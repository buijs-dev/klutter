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
package dev.buijs.klutter.gradle

import com.google.devtools.ksp.gradle.KspExtension
import com.google.devtools.ksp.gradle.KspGradleSubplugin
import dev.buijs.klutter.gradle.dsl.KlutterExtension
import dev.buijs.klutter.gradle.tasks.*
import dev.buijs.klutter.gradle.tasks.CopyAndroidAarFileGradleTask
import dev.buijs.klutter.gradle.tasks.CopyIosFrameworkGradleTask
import dev.buijs.klutter.gradle.tasks.GetDartProtocExeGradleTask
import dev.buijs.klutter.gradle.tasks.GetKradleTask
import dev.buijs.klutter.gradle.tasks.GetProtocGradleTask
import dev.buijs.klutter.kore.project.kspArgumentKlutterProjectFolder
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.tasks.TaskContainer

/**
 * Gradle plugin for Klutter Framework.
 */
class KlutterGradlePlugin: Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            tasks.registerTasks()
            plugins.applyKspPlugin()
            extensions.add("klutter", KlutterExtension(project))
            val ext = project.extensions.getByType(KspExtension::class.java)
            project.afterEvaluate {
                ext.arg(kspArgumentKlutterProjectFolder, project.rootDir.absolutePath)
            }
        }
    }
}

/**
 * Apply KSP Gradle Plugin.
 */
private fun PluginContainer.applyKspPlugin() {
    apply(KspGradleSubplugin::class.java)
}

/**
 * Register the custom Klutter tasks.
 */
private fun TaskContainer.registerTasks() {
    register("klutterCopyAarFile", CopyAndroidAarFileGradleTask::class.java)
    register("klutterCopyFramework", CopyIosFrameworkGradleTask::class.java)
    register("klutterGetKradle", GetKradleTask::class.java)
    register("klutterGetDartProtoc", GetDartProtocExeGradleTask::class.java)
    register("klutterGetProtoc", GetProtocGradleTask::class.java)
    register("klutterCompileProtoSchema", CompileProtoSchemaGradleTask::class.java)
    register("klutterCleanGeneratedProtoExt", CleanProtoExtensionsGradleTask::class.java)
}