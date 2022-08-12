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

import dev.buijs.klutter.kore.KlutterException
import dev.buijs.klutter.kore.project.Platform
import dev.buijs.klutter.kore.project.Project
import dev.buijs.klutter.kore.project.plugin
import dev.buijs.klutter.tasks.*
import mu.KotlinLogging
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

private val log = KotlinLogging.logger { }

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

    fun pathToFlutterApp(): File = project.rootProject.rootDir.resolve("app/frontend")

    fun pathToTestFolder() = project.klutterExtension().application
        ?.uiTestFolder
        ?:project.rootProject.rootDir.resolve("lib-test")

    fun pathToApplicationBuildFolder(): File = project
        .klutterExtension()
        .application
        ?.buildFolder
        ?: project
            .rootProject
            .project(":lib")
            .buildDir.resolve("libs/kompose-jvm.jar")

    fun pathToApplicationOutputFolder() = project
        .klutterExtension()
        .application
        ?.outputFolder
        ?: project.rootProject.rootDir.resolve("app/frontend/lib")

}


internal open class AdapterGeneratorGradleTask: KlutterGradleTask() {
    override fun describe() = AdapterGeneratorTask.from(project()).run()

}

internal open class BuildAndroidAndIosWithFlutterGradleTask: KlutterGradleTask() {
    override fun describe() = BuildAndroidAndIosWithFlutterTask(
        pathToFlutterApp = pathToFlutterApp(),
        pathToTestFolder = pathToTestFolder(),
    ).run()

}

internal open class BuildAndroidWithFlutterGradleTask: KlutterGradleTask() {
    override fun describe() = BuildAndroidWithFlutterTask(
        pathToFlutterApp = pathToFlutterApp(),
        pathToTestFolder = pathToTestFolder(),
    ).run()

}

internal open class BuildIosWithFlutterGradleTask: KlutterGradleTask() {
    override fun describe() = BuildIosWithFlutterTask(
        pathToFlutterApp = pathToFlutterApp(),
        pathToTestFolder = pathToTestFolder(),
    ).run()

}

internal open class BuildKlutterProjectGradleTask: KlutterGradleTask() {
    override fun describe() = BuildKlutterPluginProjectTask(project()).run()
}

internal open class CopyAndroidAarFileGradleTask: KlutterGradleTask() {
    override fun describe() {
        if(project.klutterExtension().application != null) {
            CopyAndroidAarFileTask(
                pathToAndroidAarFile = project.rootDir.resolve("lib/build/outputs/aar/lib-release.aar"),
                copyAndRenameTo = project.rootDir.resolve("app/backend/android/klutter/platform.aar"),
            ).run()
        } else {
            val pluginName = project.klutterExtension().plugin?.name
            CopyAndroidAarFileTask(
                pathToAndroidAarFile = project.rootDir.resolve("platform/build/outputs/aar/$pluginName-release.aar"),
                copyAndRenameTo = project.rootDir.resolve("android/klutter/platform.aar"),
            ).run()
        }
    }

}

internal open class CopyIosFrameworkGradleTask: KlutterGradleTask() {

    override fun describe() {
        if(project.klutterExtension().application != null) {
            CopyIosFrameworkTask(
                pathToIosFramework = project.rootDir.resolve("platform/build/fat-framework/release"),
                copyTo = project.rootDir.resolve("ios/Klutter"),
            ).run()
        } else {
            CopyIosFrameworkTask(
                pathToIosFramework = project.rootDir.resolve("lib/build/fat-framework/release"),
                copyTo = project.rootDir.resolve("app/backend/ios/Klutter"),
            ).run()
        }
    }

}

internal open class ExcludeArchsPlatformPodspecGradleTask: KlutterGradleTask() {

    override fun describe() {
        ExcludeArchsPlatformPodspecTask(project()).run()
    }

}

internal fun org.gradle.api.Project.klutterExtension(): KlutterGradleExtension {
    return extensions.getByName("klutter").let {
        if (it is KlutterGradleExtension) { it } else {
            throw IllegalStateException("klutter extension is not of the correct type")
        }
    }
}