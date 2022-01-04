package dev.buijs.klutter.gradle.tasks

import dev.buijs.klutter.gradle.utils.PigeonDartCodeGenerator
import dev.buijs.klutter.gradle.utils.runCommand
import org.gradle.internal.logging.text.StyledTextOutputFactory
import javax.inject.Inject

/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 */

open class GenerateApiTask
    @Inject constructor(styledTextOutputFactory: StyledTextOutputFactory):
KlutterGradleTask(styledTextOutputFactory)
{
    override fun describe() {
        val dto = ext.getServicesDto()
        if(dto == null) {
            logger.error("Services not configured in build.gradle.kts file. " +
                    "Use 'Services { } in klutter block to configure one ore " +
                    "more API's, Data Classes and/or Enumerations.' ")
        } else {
            val root = flutter()

            PigeonDartCodeGenerator(
                flutter = root,
                serviceDTO = dto
            ).generate()

            val flutterlib = root.resolve("lib/generated")
            if(!flutterlib.exists()) flutterlib.mkdirs()

            val javalib = root.resolve("android/app/src/main/java/dev/buijs/klutter/generated")
            if(!javalib.exists()) javalib.mkdirs()

            //                --objc_header_out ios/Runner/pigeon.h \
            //                --objc_source_out ios/Runner/pigeon.m \
            val command =
                """flutter pub run pigeon \
                --input $root/klutter/.klutter/pigeon.dart \
                --dart_out ${flutterlib}/adapter.dart \
                --java_out $javalib/Adapter.java \
                --java_package "dev.buijs.klutter.generated""""
            val pigeonlogging = command.runCommand(root)
            logger.info(pigeonlogging?:"")
        }
    }
}