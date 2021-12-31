package dev.buijs.klutter.gradle

import dev.buijs.klutter.core.adapter.service.KlutterServiceBuilder
import dev.buijs.klutter.core.adapter.service.KlutterServiceDTO
import dev.buijs.klutter.core.multiplatform.MultiplatformBuilder
import dev.buijs.klutter.core.multiplatform.MultiplatformDTO
import dev.buijs.klutter.gradle.tasks.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File


/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 */
private const val ADAPTER_EXTENSION_NAME = "klutter"

class KlutterAdapterPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(ADAPTER_EXTENSION_NAME, KlutterAdapterExtension::class.java)
        project.tasks.register("generateAdapter", AdapterTask::class.java)
        project.tasks.register("generateAndroidBuildGradle", GenerateAndroidGradleTask::class.java)
        project.tasks.register("sync", ConfigProducerTask::class.java)
        project.tasks.register("generateApi", KlutterGeneratePigeonsTask::class.java)
        project.tasks.register("buildDebug", BuildDebugTask::class.java)
    }
}

internal fun Project.adapter(): KlutterAdapterExtension =
    extensions.getByName(ADAPTER_EXTENSION_NAME) as? KlutterAdapterExtension
        ?: throw IllegalStateException("$ADAPTER_EXTENSION_NAME is not of the correct type")

open class KlutterAdapterExtension(project: Project) {

    private val root = project.rootDir
    private var servicesDto: KlutterServiceDTO? = null
    private var multiplatformDto: MultiplatformDTO? = null

    var flutter: File? = null
    var podspec: File? = null
    var modules: List<File> = emptyList()

    fun services(lambda: KlutterServiceBuilder.() -> Unit) {
        servicesDto = KlutterServiceBuilder().apply(lambda).build()
    }

    fun multiplatform(lambda: MultiplatformBuilder.() -> Unit) {
        multiplatformDto = MultiplatformBuilder().apply(lambda).build()
    }

    internal fun getServicesDto() = servicesDto

    internal fun getMultiplatformDto() = multiplatformDto
}