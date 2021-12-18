package dev.buijs.klutter.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File


/**
 * By Gillian Buijs
 *
 * Contact me: https://buijs.dev
 */
private const val EXTENSION_NAME = "klutter"

class KlutterAdapterPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.create("generate", KlutterAdapterTask::class.java) {}
        project.extensions.create(
            EXTENSION_NAME,
            KlutterAdapterExtension::class.java,
            project)
    }

}

internal fun Project.klutteradapter(): KlutterAdapterExtension =
    extensions.getByName(EXTENSION_NAME) as? KlutterAdapterExtension
        ?: throw IllegalStateException("$EXTENSION_NAME is not of the correct type")

open class KlutterAdapterExtension(project: Project) {

    private val root = project.rootDir

    var sources: List<File> = emptyList()
    var android: File? = null
    var ios: File? = null
    var flutter: File? = null

}