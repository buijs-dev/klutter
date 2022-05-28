package dev.buijs.klutter.plugins.gradle

import dev.buijs.klutter.plugins.gradle.dsl.*
import dev.buijs.klutter.plugins.gradle.tasks.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Internal
import java.io.File

/**
 * @author Gillian Buijs
 */
private const val EXTENSION_NAME = "klutter"

class KlutterGradlePlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(EXTENSION_NAME, KlutterGradleExtension::class.java)
        project.tasks.register("generateAdapters", GenerateAdapterGradleTask::class.java)
        project.tasks.register("updateProject", UpdateProjectGradleTask::class.java)
        project.tasks.register("updatePlatformPodspec", UpdatePlatformPodspecGradleTask::class.java)
        project.tasks.register("buildFlutterPackage", CreatePublishPackageGradleTask::class.java)
        project.tasks.register("buildKlutterPlugin", CreatePluginProjectGradleTask::class.java)
    }
}

internal fun Project.adapter(): KlutterGradleExtension =
    extensions.getByName(EXTENSION_NAME) as? KlutterGradleExtension
        ?: throw IllegalStateException("$EXTENSION_NAME is not of the correct type")

open class KlutterGradleExtension {

    var root: File? = null

    @Internal
    internal var appInfo: KlutterAppInfoDTO? = null

    @Internal
    internal var plugin: KlutterPluginDTO? = null

    fun app(lambda: KlutterAppInfoBuilder.() -> Unit) {
        appInfo = KlutterAppInfoBuilder().apply(lambda).build()
    }

    fun plugin(lambda: KlutterPluginBuilder.() -> Unit) {
        plugin = KlutterPluginBuilder().apply(lambda).build()
    }

}