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
package dev.buijs.klutter.gradle.tasks

import dev.buijs.klutter.gradle.dsl.KlutterGradleDSL
import dev.buijs.klutter.kore.KlutterException
import dev.buijs.klutter.kore.KlutterTask
import dev.buijs.klutter.kore.project.Platform
import dev.buijs.klutter.kore.project.Project
import dev.buijs.klutter.kore.project.plugin
import mu.KotlinLogging
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File

private val log = KotlinLogging.logger { }

internal typealias project = org.gradle.api.Project

/**
 * Parent of all Gradle Tasks.
 */
internal abstract class AbstractTask(): DefaultTask() {

    init { group = "klutter" }

    internal abstract fun klutterTask(): KlutterTask

    @TaskAction
    fun execute() = klutterTask().run()

    fun project(): Project {
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

    internal fun pathToFlutterApp(): File = project
        .rootProject
        .rootDir
        .resolve("app/frontend")

    internal fun pathToTestFolder() = project.klutterExtension().application
        ?.uiTestFolder
        ?:project.rootProject.rootDir.resolve("lib-test")

    @Internal
    internal fun isApplication() = project
        .klutterExtension()
        .application != null
}

internal fun org.gradle.api.Project.klutterExtension(): KlutterGradleDSL {
    return extensions.getByName("klutter").let {
        if (it is KlutterGradleDSL) { it } else {
            throw IllegalStateException("klutter extension is not of the correct type")
        }
    }
}