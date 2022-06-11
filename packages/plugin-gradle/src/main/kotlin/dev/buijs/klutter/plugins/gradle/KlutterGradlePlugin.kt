package dev.buijs.klutter.plugins.gradle

import dev.buijs.klutter.core.excludeArm64
import dev.buijs.klutter.core.project.plugin
import dev.buijs.klutter.core.tasks.AdapterGeneratorTask
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Gradle plugin for Klutter Framework with the following tasks:
 * - klutterGenerateAdapters
 * - klutterExcludeArchsPlatformPodspec
 */
class KlutterGradlePlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("klutter", KlutterGradleExtension::class.java)

        project.tasks.register("klutterGenerateAdapters", GenerateAdapters::class.java)

        project.tasks.register("klutterExcludeArchsPlatformPodspec", ExcludeArchsPlatformPodspec::class.java)
    }
}

/**
 * Task to generate method-channel boilerplate in ios and android folders.
 */
internal open class GenerateAdapters: KlutterGradleTask() {
    override fun describe() {

        val pathToRoot = ext.root?.absolutePath ?: project.rootDir.path

        val pluginName = ext.plugin?.name?.ifBlank { null }

        val project = pluginName
            ?.let { pathToRoot.plugin(it) }
            ?:pathToRoot.plugin()

        AdapterGeneratorTask(
            ios = project.ios,
            root = project.root,
            android = project.android,
            platform = project.platform,
        ).run()
    }

}

/**
 * Task to edit the podspec file in the root/platform folder.
 */
internal open class ExcludeArchsPlatformPodspec: KlutterGradleTask() {
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

}

@TaskAction
internal fun KlutterGradleTask.execute() = describe()

internal fun KlutterGradleTask.project() =
    (ext.root ?: project.rootProject.projectDir).plugin()

internal fun Project.adapter(): KlutterGradleExtension =
    extensions.getByName("klutter").let {
        if(it is KlutterGradleExtension) { it } else {
            throw IllegalStateException("klutter extension is not of the correct type")
        }
}
