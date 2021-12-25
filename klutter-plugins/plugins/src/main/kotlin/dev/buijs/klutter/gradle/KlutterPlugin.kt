package dev.buijs.klutter.gradle

import dev.buijs.klutter.core.adapter.service.KlutterServiceBuilder
import dev.buijs.klutter.core.adapter.service.KlutterServiceDTO
import dev.buijs.klutter.core.adapter.service.KlutterServicesDSL
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
        project.tasks.create("generateAdapter", KlutterAdapterTask::class.java) {}
        project.tasks.create("produceConfig", KlutterProduceConfigTask::class.java) {}
        project.tasks.create("generateApi", KlutterGeneratePigeonsTask::class.java) {}
        project.extensions.create(ADAPTER_EXTENSION_NAME, KlutterAdapterExtension::class.java, project)
    }
}

internal fun Project.adapter(): KlutterAdapterExtension =
    extensions.getByName(ADAPTER_EXTENSION_NAME) as? KlutterAdapterExtension
        ?: throw IllegalStateException("$ADAPTER_EXTENSION_NAME is not of the correct type")

open class KlutterAdapterExtension(project: Project) {

    private val root = project.rootDir
    private var servicesDto: KlutterServiceDTO? = null

    var sources: List<File> = emptyList()
    var android: File? = null
    var ios: File? = null
    var flutter: File? = null
    var podspec: File? = null
    var modules: List<File> = emptyList()

    fun services(lambda: KlutterServiceBuilder.() -> Unit) {
        servicesDto = KlutterServiceBuilder().apply(lambda).build()
    }

    internal fun getServicesDto() = servicesDto

}