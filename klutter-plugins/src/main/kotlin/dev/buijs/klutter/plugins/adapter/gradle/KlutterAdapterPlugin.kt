package dev.buijs.klutter.plugins.adapter.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File


/**
 * By Gillian Buijs
 *
 * Contact me: https://buijs.dev
 */
private const val EXTENSION_NAME = "klutteradapter"

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

//internal class GradleProperty<T, V>(
//    project: Project,
//    type: Class<V>,
//    default: V? = null
//) {
//    val property = project.objects.property(type).apply {
//        set(default)
//    }
//
//    operator fun getValue(thisRef: T, property: KProperty<*>): V =
//        this.property.get()
//
//    operator fun setValue(thisRef: T, property: KProperty<*>, value: V) =
//        this.property.set(value)
//}
