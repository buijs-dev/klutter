package dev.buijs.klutter.plugins.gradle

import dev.buijs.klutter.plugins.gradle.dsl.*
import dev.buijs.klutter.plugins.gradle.tasks.adapter.GenerateAdapterTask
import dev.buijs.klutter.plugins.gradle.tasks.install.UpdatePlatformPodspecTask
import dev.buijs.klutter.plugins.gradle.tasks.android.GenerateAndroidBuildGradleTask
import dev.buijs.klutter.plugins.gradle.tasks.ios.GenerateIosPodfile
import dev.buijs.klutter.plugins.gradle.tasks.setup.ProjectSetupTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File


/**
 * @author Gillian Buijs
 */
private const val EXTENSION_NAME = "klutter"

class KlutterPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(EXTENSION_NAME, KlutterExtension::class.java)
        project.tasks.register("generateAdapters", GenerateAdapterTask::class.java)
        project.tasks.register("generateAndroidBuildGradle", GenerateAndroidBuildGradleTask::class.java)
        project.tasks.register("generateIOSPodfile", GenerateIosPodfile::class.java)
        project.tasks.register("updatePlatformPodspec", UpdatePlatformPodspecTask::class.java)
        project.tasks.register("setupProject", ProjectSetupTask::class.java)
    }
}

internal fun Project.adapter(): KlutterExtension =
    extensions.getByName(EXTENSION_NAME) as? KlutterExtension
        ?: throw IllegalStateException("$EXTENSION_NAME is not of the correct type")

open class KlutterExtension(private val project: Project) {

    internal var root: File = project.rootProject.projectDir

    private var multiplatformDto: KlutterMultiplatformDTO? = null
    private var repositoriesDto: KlutterRepositoriesDTO? = null
    private var modulesDto: KlutterModulesDTO? = null
    private var iosDTO: KlutterIosDTO? = null
    private var appConfig: KlutterAppConfigDTO? = null
    private var gradleVersion: String? = null

    fun root(file: String){ root = File(file) }

    fun multiplatform(lambda: KlutterMultiplatformBuilder.() -> Unit) {
        multiplatformDto = KlutterMultiplatformBuilder().apply(lambda).build()
    }

    fun repositories(lambda: KlutterRepositoriesBuilder.() -> Unit) {
        repositoriesDto = KlutterRepositoriesBuilder().apply(lambda).build()
    }

    fun modules(lambda: KlutterModulesBuilder.() -> Unit) {
        modulesDto = KlutterModulesBuilder(project.rootProject.rootDir).apply(lambda).build()
    }

    fun ios(lambda: KlutterIosBuilder.() -> Unit) {
        iosDTO = KlutterIosBuilder().apply(lambda).build()
    }

    fun app(lambda: KlutterAppConfigBuilder.() -> Unit) {
        appConfig = KlutterAppConfigBuilder().apply(lambda).build()
    }

    fun gradle(version: String) {
        gradleVersion = version
    }

    internal fun getGradleVersion() = gradleVersion

    internal fun getAppName() = appConfig?.name

    internal fun getMultiplatformDto() = multiplatformDto

    internal fun getRepositoriesDto() = repositoriesDto

    internal fun getModulesDto() = modulesDto

    internal fun getIosDto() = iosDTO

}