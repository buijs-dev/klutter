package dev.buijs.klutter.plugins.gradle

import dev.buijs.klutter.plugins.gradle.dsl.*
import dev.buijs.klutter.plugins.gradle.tasks.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File


/**
 * @author Gillian Buijs
 */
private const val EXTENSION_NAME = "klutter"
private const val KLUTTER_VERSION = "2022-alpha-1"

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

    private var appInfo: KlutterAppInfoDTO? = null

    fun app(lambda: KlutterAppInfoBuilder.() -> Unit) {
        appInfo = KlutterAppInfoBuilder().apply(lambda).build()
    }

    internal fun getAppInfo() = appInfo

    internal fun getKlutterVersion() = KLUTTER_VERSION
}