package dev.buijs.klutter.plugins.gradle

import dev.buijs.klutter.plugins.gradle.dsl.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Internal
import java.io.File
import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.shared.excludeArm64
import dev.buijs.klutter.core.tasks.GenerateAdapterTask
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Gradle plugin for Klutter Framework with the following tasks:
 * - generateAdapters
 * - updatePlatformPodspec
 */
class KlutterGradlePlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("klutter", KlutterGradleExtension::class.java)

        project.tasks.register("generateAdapters", GenerateAdapters::class.java)

        project.tasks.register("updatePlatformPodspec", UpdatePlatformPodspec::class.java)
    }
}

/**
 * Task to generate method-channel boilerplate in ios and android folders.
 */
internal open class GenerateAdapters: KlutterGradleTask() {
    override fun describe() {
        val pathToRoot = ext.root?.absolutePath ?: project.rootDir.path
        val pluginName = ext.plugin?.name
        val project = pluginName
            ?.let { pathToRoot.klutterProject(it) }
            ?:pathToRoot.klutterProject()

        GenerateAdapterTask(
            root = project.root,
            android = project.android,
            ios = project.ios,
            platform = project.platform,
        ).run()
    }

}

/**
 * Task to edit the podspec file in the root/platform folder.
 */
internal open class UpdatePlatformPodspec: KlutterGradleTask() {
    override fun describe() {
        project().platform.podspec().excludeArm64()
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
    val ext: KlutterGradleExtension = project.adapter()

    /**
     * The implementing class must describe what the task does by implementing this function.
     */
    abstract fun describe()

    @TaskAction
    fun execute() = describe()

    fun project() = Root(
        ext.root ?: throw KlutterException("Path to root folder is not set.")
    ).klutterProject()


}

internal fun Project.adapter(): KlutterGradleExtension =
    extensions.getByName("klutter") as? KlutterGradleExtension
        ?: throw IllegalStateException("klutter extension is not of the correct type")
