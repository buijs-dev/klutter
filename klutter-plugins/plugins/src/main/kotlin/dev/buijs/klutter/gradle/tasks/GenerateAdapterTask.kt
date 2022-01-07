package dev.buijs.klutter.gradle.tasks

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import com.intellij.openapi.util.Disposer
import dev.buijs.klutter.core.adapter.KlutterAdapterProducer
import dev.buijs.klutter.gradle.KlutterTask
import org.gradle.internal.logging.text.StyledTextOutputFactory
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import javax.inject.Inject

/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 */
open class GenerateAdapterTask
@Inject constructor(styledTextOutputFactory: StyledTextOutputFactory)
    : KlutterTask(styledTextOutputFactory) {

    private val context by lazy {
        val config = CompilerConfiguration()
        config.put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        KotlinCoreEnvironment.createForProduction(Disposer.newDisposable(), config, EnvironmentConfigFiles.JVM_CONFIG_FILES).project
    }

    override fun describe() {
        KlutterAdapterProducer(
            context = context,
            project = project(),
            androidManifest = androidManifest(),
            iosVersion = iosVersion(),
            podName = project().kmp.podspec().nameWithoutExtension
        ).produce().also { logger.merge(it) }
    }


}