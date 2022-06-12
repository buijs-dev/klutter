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
    override fun describe() = project().let {
        AdapterGeneratorTask(
            ios = it.ios,
            root = it.root,
            android = it.android,
            platform = it.platform,
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

    /**
     * The implementing class must describe what the task does by implementing this function.
     */
    open fun describe() { }

    @TaskAction
    fun execute() = describe()

    fun project() =
        (project.adapter().root ?: project.rootProject.projectDir).plugin()

}

internal fun Project.adapter(): KlutterGradleExtension {
    return extensions.getByName("klutter").let {
        if (it is KlutterGradleExtension) { it } else {
            throw IllegalStateException("klutter extension is not of the correct type")
        }
    }
}
