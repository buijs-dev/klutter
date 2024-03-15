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
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Gradle plugin for Klutter Framework.
 */
class KlutterGradlePlugin: Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            tasks.registerTasks()
            plugins.applyKspPlugin()
            extensions.add("klutter", KlutterExtension(project))
            val ksp = project.extensions.getByType(KspExtension::class.java)
            project.afterEvaluate {
                // KMP
                val kmp = try {
                    project.extensions.getByType(KotlinMultiplatformExtension::class.java)
                } catch (_: Exception) {
                    null
                }

                if (kmp != null) {
                    kmp.sourceSets
                        .getByName("commonMain")
                        .kotlin
                        .srcDir(layout.buildDirectory.file("generated/ksp/metadata/commonMain/kotlin"))
                    tasks.bindPostBuildTasks()
                }

                // KSP
                ksp.arg(kspArgumentKlutterProjectFolder, project.rootDir.absolutePath)
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
    register(CopyAndroidAarFileGradleTask.gradleTaskName, CopyAndroidAarFileGradleTask::class.java)
    register(CopyIosFrameworkGradleTask.gradleTaskName, CopyIosFrameworkGradleTask::class.java)
    register(GetKradleTask.gradleTaskName, GetKradleTask::class.java)
    register(GetDartProtocExeGradleTask.gradleTaskName, GetDartProtocExeGradleTask::class.java)
    register(GetProtocGradleTask.gradleTaskName, GetProtocGradleTask::class.java)
    register(CompileProtoSchemaGradleTask.gradleTaskName, CompileProtoSchemaGradleTask::class.java)
    register(GenerateFlutterLibGradleTask.gradleTaskName, GenerateFlutterLibGradleTask::class.java)
}

private fun TaskContainer.bindPostBuildTasks() {
    getByName("build").setFinalizedBy(
       listOf(CopyAndroidAarFileGradleTask.gradleTaskName,
           CompileProtoSchemaGradleTask.gradleTaskName,
           "assemblePlatformReleaseXCFramework",
           "klutterGenerateProtoSchemas"
       ))

    getByName("assemblePlatformReleaseXCFramework")
        .setFinalizedBy(listOf(getByName(CopyIosFrameworkGradleTask.gradleTaskName)))

    getByName("klutterGenerateProtoSchemas")
        .setFinalizedBy(listOf("klutterCompileProtoSchemas"))
}