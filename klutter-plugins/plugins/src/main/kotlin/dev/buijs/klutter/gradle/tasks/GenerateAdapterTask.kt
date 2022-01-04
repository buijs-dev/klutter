package dev.buijs.klutter.gradle.tasks

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import com.intellij.openapi.util.Disposer
import dev.buijs.klutter.core.adapter.KlutterAdapterProducer
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
    : KlutterGradleTask(styledTextOutputFactory) {

    private val context by lazy {
        val config = CompilerConfiguration()
        config.put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        KotlinCoreEnvironment.createForProduction(Disposer.newDisposable(), config, EnvironmentConfigFiles.JVM_CONFIG_FILES).project
    }

    override fun describe() {
        val logging = KlutterAdapterProducer(
            context = context,
            kmp = kmp(),
            flutter = flutter(),
            android = android(),
            androidManifest = androidManifest(),
            iosVersion = iosVersion(),
            podName = podspec().nameWithoutExtension
        ).produce()

        logger.merge(logging)
    }


}