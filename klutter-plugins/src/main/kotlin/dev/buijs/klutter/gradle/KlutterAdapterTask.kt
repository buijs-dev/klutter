package dev.buijs.klutter.gradle

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import com.intellij.openapi.util.Disposer
import dev.buijs.klutter.core.KlutterCodeGenerationException
import dev.buijs.klutter.core.adapter.KlutterAdapterCodeGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.logging.text.StyledTextOutputFactory
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import javax.inject.Inject

/**
 * By Gillian Buijs
 *
 * Contact me: https://buijs.dev
 */
open class KlutterAdapterTask
@Inject constructor(
    private val styledTextOutputFactory: StyledTextOutputFactory
): DefaultTask() {

    private val context by lazy {
        val config = CompilerConfiguration()
        config.put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        KotlinCoreEnvironment.createForProduction(
            Disposer.newDisposable(),
            config,
            EnvironmentConfigFiles.JVM_CONFIG_FILES
        ).project
    }

    init {
        group = "klutter"
    }

    @TaskAction
    fun generate() {
        val ext = project.klutteradapter()
        val sources = ext.sources?: throw KlutterCodeGenerationException("Missing path to android/app folder in the flutter root project")

        sources.forEach {
            if(!it.exists()) throw KlutterCodeGenerationException("Source directory does not exist: ${it.absolutePath}")
        }

        val flutter = ext.flutter?: throw KlutterCodeGenerationException("blabla")

        val android = ext.android?: throw KlutterCodeGenerationException("Missing path to android/app folder in the flutter root project")

        if(!android.exists()) throw KlutterCodeGenerationException("Source directory does not exist: ${android.absolutePath}")

        val ios = ext.ios

        val podspec = ext.podspec ?: throw KlutterCodeGenerationException("Missing path to podspec file in the KMP project")

        val logs = KlutterAdapterCodeGenerator(
            context = context,
            sources = sources,
            android = android,
            flutter = flutter,
            podspec = podspec
        ).generate()

        val output = styledTextOutputFactory.create(javaClass.name)
        GradleLoggingWrapper(logs, output).sout()
    }


}