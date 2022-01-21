package dev.buijs.klutter.plugins.gradle

import dev.buijs.klutter.plugins.gradle.dsl.*
import dev.buijs.klutter.plugins.gradle.tasks.config.SynchronizeTask
import dev.buijs.klutter.plugins.gradle.tasks.adapter.GenerateAdapterTask
import dev.buijs.klutter.plugins.gradle.tasks.build.BuildDebugTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File


/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 */
private const val EXTENSION_NAME = "klutter"

class KlutterPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(EXTENSION_NAME, KlutterExtension::class.java)
        project.tasks.register("synchronize", SynchronizeTask::class.java)
        project.tasks.register("generate adapters", GenerateAdapterTask::class.java)
        project.tasks.register("build debug", BuildDebugTask::class.java)
    }
}

internal fun Project.adapter(): KlutterExtension =
    extensions.getByName(EXTENSION_NAME) as? KlutterExtension
        ?: throw IllegalStateException("$EXTENSION_NAME is not of the correct type")

open class KlutterExtension(private val project: Project) {

    private var multiplatformDto: KlutterMultiplatformDTO? = null
    private var repositoriesDto: KlutterRepositoriesDTO? = null
    private var modulesDto: KlutterModulesDTO? = null
    private var iosDTO: KlutterIosDTO? = null
    internal var root: File = project.rootProject.projectDir

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

    internal fun getMultiplatformDto() = multiplatformDto

    internal fun getRepositoriesDto() = repositoriesDto

    internal fun getModulesDto() = modulesDto

    internal fun getIosDto() = iosDTO
}