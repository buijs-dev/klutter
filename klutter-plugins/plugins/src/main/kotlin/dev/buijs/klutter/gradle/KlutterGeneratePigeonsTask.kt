package dev.buijs.klutter.gradle

import dev.buijs.klutter.core.adapter.service.PigeonDartCodeGenerator
import dev.buijs.klutter.core.log.KlutterLogging
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.logging.text.StyledTextOutputFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * @author Gillian Buijs
 *
 * Contact me: https://buijs.dev
 */
open class KlutterGeneratePigeonsTask @Inject constructor(
    private val styledTextOutputFactory: StyledTextOutputFactory
): DefaultTask() {

    init {
        group = "klutter"
    }

    @TaskAction
    fun generateApi() {
        val ext = project.adapter()
        val ios = ext.ios
        val android = ext.android
        val flutter = ext.flutter ?: throw Exception("flutter must not be null")
        val logger = KlutterLogging()

        val dto = ext.getServicesDto()
        if(dto == null) {
            logger.error("Services not configured in build.gradle.kts file. " +
                    "Use 'Services { } in klutter block to configure one ore " +
                    "more API's, Data Classes and/or Enumerations.' ")
        } else {
            PigeonDartCodeGenerator(
                flutter = flutter,
                serviceDTO = dto
            ).generate()

            val dart = flutter.resolve("lib/generated")
            if(!dart.exists()) dart.mkdirs()
            //                --objc_header_out ios/Runner/pigeon.h \
            //                --objc_source_out ios/Runner/pigeon.m \
            val pigeonlogging = """flutter pub run pigeon \
                --input $flutter/klutter/.klutter/pigeon.dart \
                --dart_out $dart/adapter.dart \
                --java_out $flutter/android/app/src/main/java/dev/buijs/klutter/generated/Adapter.java \
                --java_package "dev.buijs.klutter.generated.adapter"""".runCommand(flutter)

            logger.info(pigeonlogging?:"")
        }

        val output = styledTextOutputFactory.create(javaClass.name)
        GradleLoggingWrapper(logger, output).sout()
    }

    private fun String.runCommand(workingDir: File): String? {
        return try {
            val parts = this.split("\\s".toRegex())
            val proc = ProcessBuilder(*parts.toTypedArray())
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

            proc.waitFor(30, TimeUnit.SECONDS)
            proc.inputStream.bufferedReader().readText()
        } catch(e: IOException) {
            e.printStackTrace()
            null
        }
    }
}