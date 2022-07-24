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

package dev.buijs.klutter.plugins.gradle

import dev.buijs.klutter.core.KlutterException
import dev.buijs.klutter.core.project.*
import dev.buijs.klutter.plugins.gradle.tasks.ExcludeArchsPlatformPodspec
import dev.buijs.klutter.plugins.gradle.tasks.GenerateAdapters
import dev.buijs.klutter.plugins.gradle.tasks.GenerateUI
import mu.KotlinLogging
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File

private val log = KotlinLogging.logger { }

/**
 * Gradle plugin for Klutter Framework with the following tasks:
 * - klutterGenerateAdapters
 * - klutterGenerateUI
 * - klutterExcludeArchsPlatformPodspec
 */
class KlutterGradlePlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("klutter", KlutterGradleExtension::class.java)
        project.tasks.register("klutterGenerateAdapters", GenerateAdapters::class.java)
        project.tasks.register("klutterGenerateUI", GenerateUI::class.java)
        project.tasks.register("klutterExcludeArchsPlatformPodspec", ExcludeArchsPlatformPodspec::class.java)
    }
}

/**
 * Glue for the DSL used in a build.gradle(.kts) file and the Klutter tasks.
 */
open class KlutterGradleExtension {

    var root: File? = null

    @Internal
    internal var plugin: KlutterPluginDTO? = null

    @Internal
    internal var application: KlutterApplicationDTO? = null

    fun plugin(lambda: KlutterPluginBuilder.() -> Unit) {
        plugin = KlutterPluginBuilder().apply(lambda).build()
    }

    fun application(lambda: KlutterApplicationBuilder.() -> Unit) {
        application = KlutterApplicationBuilder().apply(lambda).build()
    }

}

/**
 * Parent of all Gradle Tasks.
 */
internal abstract class KlutterGradleTask: DefaultTask() {

    init {
        group = "klutter"
    }

    /**
     * The implementing class must describe what the task does by implementing this function.
     */
    abstract fun describe()

    @TaskAction
    fun execute() = describe()

    fun project(): dev.buijs.klutter.core.project.Project {
        val ext = project.klutterExtension()

        val root = ext.root ?: project.rootProject.projectDir
        val plugin = ext.plugin
        val application = ext.application

        return when {
            plugin != null && application != null -> {
                throw KlutterException(
                    "Both plugin and application are set in klutter DSL but only 1 can be used."
                )
            }

            application != null -> {
                log.info { "Klutter Gradle configured as application." }
                val appRoot = application.root ?: project.rootProject.rootDir.resolve("app/backend")
                log.info { "Processing Klutter app with root: ${appRoot.absolutePath}" }
                val appProject = appRoot.plugin()
                Project(
                    root = appProject.root,
                    ios = appProject.ios,
                    android = appProject.android,
                    platform = Platform(
                        folder = root.resolve("lib"),
                        pluginName = "lib"
                    ),
                )
            }

            else -> {
                log.info { "Klutter Gradle configured as plugin." }
                root.plugin()
            }
        }

    }

}

internal fun Project.klutterExtension(): KlutterGradleExtension {
    return extensions.getByName("klutter").let {
        if (it is KlutterGradleExtension) { it } else {
            throw IllegalStateException("klutter extension is not of the correct type")
        }
    }
}
