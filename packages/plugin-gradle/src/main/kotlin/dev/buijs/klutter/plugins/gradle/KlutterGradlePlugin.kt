package dev.buijs.klutter.plugins.gradle

import dev.buijs.klutter.plugins.gradle.dsl.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Internal
import java.io.File
import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.tasks.GenerateAdapterTask
import dev.buijs.klutter.core.tasks.UpdatePlatformPodspecTask
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

private const val EXTENSION_NAME = "klutter"

/**
 * Gradle plugin for Klutter Framework with the following tasks:
 * - generateAdapters
 * - updatePlatformPodspec
 */
class KlutterGradlePlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(EXTENSION_NAME, KlutterGradleExtension::class.java)

        /**
         * Task to generate method-channel boilerplate in ios and android folders.
         */
        project.register("generateAdapters", object: KlutterGradleTask() {
            override fun describe() {
                GenerateAdapterTask.create(
                    ext.root?.absolutePath ?: project.rootDir.path,
                    ext.plugin?.name,
                ).run()
            }
        })

        /**
         * Task to edit the podspec file in the root/platform folder.
         */
        project.register("updatePlatformPodspec", object: KlutterGradleTask() {
            override fun describe() {
                UpdatePlatformPodspecTask(project()).run()
            }
        })
    }

    /**
     * Register a new task which can be used after applying the Klutter Gradle plugin.
     */
    private fun Project.register(name: String, task: KlutterGradleTask) {
        project.tasks.register(name, task::class.java)
    }

}

/**
 * Glue for the DSL used in a build.gradle(.kts) file and the Klutter tasks.
 */
open class KlutterGradleExtension {

    var root: File? = null

    @Internal
    internal var plugin: KlutterPluginDTO? = null

    fun plugin(lambda: KlutterPluginBuilder.() -> Unit) {
        plugin = KlutterPluginBuilder().apply(lambda).build()
    }

}

/**
 * Parent of all Gradle Tasks.
 */
internal abstract class KlutterGradleTask: DefaultTask() {

    init {
        group = "klutter"
    }

    @Internal
    val ext = project.adapter()

    /**
     * The implementing class must describe what the task does by implementing this function.
     */
    abstract fun describe()

    @TaskAction
    fun execute() = describe()

    fun project() = KlutterProject.create(
        Root(ext.root ?: throw KlutterException("Path to root folder is not set."))
    )

    private fun Project.adapter(): KlutterGradleExtension =
        extensions.getByName(EXTENSION_NAME) as? KlutterGradleExtension
            ?: throw IllegalStateException("$EXTENSION_NAME is not of the correct type")
}