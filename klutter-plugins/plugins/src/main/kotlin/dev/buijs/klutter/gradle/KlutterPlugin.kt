package dev.buijs.klutter.gradle

import dev.buijs.klutter.core.adapter.service.KlutterServiceBuilder
import dev.buijs.klutter.core.adapter.service.KlutterServiceDTO
import dev.buijs.klutter.gradle.tasks.AdapterTask
import dev.buijs.klutter.gradle.tasks.KlutterGeneratePigeonsTask
import dev.buijs.klutter.gradle.tasks.ConfigProducerTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File


/**
 * @author Gillian Buijs
 *
 * Contact me: https://buijs.dev
 */
private const val ADAPTER_EXTENSION_NAME = "klutter"

class KlutterAdapterPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(ADAPTER_EXTENSION_NAME, KlutterAdapterExtension::class.java)
        project.tasks.register("generateAdapter", AdapterTask::class.java)
        project.tasks.register("generateAndroidBuildGradle", AdapterTask::class.java)
        project.tasks.register("produceConfig", ConfigProducerTask::class.java)
        project.tasks.register("generateApi", KlutterGeneratePigeonsTask::class.java)
    }
}

internal fun Project.adapter(): KlutterAdapterExtension =
    extensions.getByName(ADAPTER_EXTENSION_NAME) as? KlutterAdapterExtension
        ?: throw IllegalStateException("$ADAPTER_EXTENSION_NAME is not of the correct type")

open class KlutterAdapterExtension(project: Project) {

    private val root = project.rootDir
    private var servicesDto: KlutterServiceDTO? = null

    var sources: List<File> = emptyList()
    var flutter: File? = null
    var podspec: File? = null
    var modules: List<File> = emptyList()

    fun services(lambda: KlutterServiceBuilder.() -> Unit) {
        servicesDto = KlutterServiceBuilder().apply(lambda).build()
    }

    internal fun getServicesDto() = servicesDto

}