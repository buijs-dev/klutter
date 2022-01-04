package dev.buijs.klutter.core.flutter

import dev.buijs.klutter.core.KlutterLogger
import dev.buijs.klutter.core.KlutterVisitor
import java.io.File

/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 */
internal class IosPodspecVisitor(private val podspec: File): KlutterVisitor {

    override fun visit(): KlutterLogger {
        val logger = KlutterLogger()
        val podspecName = podspec.name.substringBefore(".podspec")
        val newPodspecContent = podspec.readLines().asSequence()
            .map { line ->
                when {
                    line.contains("spec.ios.deployment_target") -> {
                        logger.debug("Adding lines to file $podspec to exclude iphonesimulator SDK")
                        "    $line \r\n" +
                                "    #These lines are added by the Klutter Framework to enable the app to run on a simulator\r\n" +
                                "    spec.pod_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' } \r\n" +
                                "    spec.user_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' } \r\n"
                    }

                    line.filter { !it.isWhitespace() }.contains("spec.vendored_frameworks=\"build") -> {
                        logger.debug("Changing line in $podspec to use fat-framework")
                        "    #This line is altered by Klutter Framework to use the fat-framework\r\n" +
                                "spec.vendored_frameworks      = \"build/fat-framework/debug/${podspecName}.framework\""
                    }

                    else -> line
                }
            }
            .toList()

        newPodspecContent.joinToString(separator = "\n").also {
            podspec.writeText(it)
            logger.debug("Written content to file $podspec:\r\n$it")
        }

        return logger
    }

}