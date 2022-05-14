package dev.buijs.klutter.plugins.gradle.tasks

import dev.buijs.klutter.core.*
import dev.buijs.klutter.plugins.gradle.adapter
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.logging.text.StyledTextOutputFactory
import javax.inject.Inject

/**
 * Parent of all Gradle Tasks.
 * This class groups all implementing class under the <b>klutter</> group and gives access to the klutter configuration.
 *
 * @author Gillian Buijs
 */
abstract class KlutterGradleTask: DefaultTask() {
    init { group = "klutter" }

    @Internal
    val ext = project.adapter()

    /**
     * The implementing class must describe what the task does by implementing this function.
     */
    abstract fun describe()

    @TaskAction
    fun execute() = describe()

    fun iosVersion() = ext.getIos()?.version?:"13.0"

    fun appInfo() = ext.getAppInfo() ?: throw KlutterConfigException("App Config missing!")

    fun project() = KlutterProjectFactory.create(Root(ext.root ?: throw KlutterGradleException("Invalid Klutter Project.")))
        ?: throw KlutterGradleException("Invalid Klutter Project.")

}