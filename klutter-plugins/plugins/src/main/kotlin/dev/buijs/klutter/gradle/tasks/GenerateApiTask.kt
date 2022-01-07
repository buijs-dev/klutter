package dev.buijs.klutter.gradle.tasks

import dev.buijs.klutter.gradle.KlutterTask
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
KlutterTask(styledTextOutputFactory)
{
    override fun describe() {
        val dto = ext.getServicesDto()
        if(dto == null) {
            logger.error("Services not configured in build.gradle.kts file. " +
                    "Use 'Services { } in klutter block to configure one or " +
                    "more API's, Data Classes and/or Enumerations.' ")
        } else {
            val project = project()

            PigeonDartCodeGenerator(
                klutter = project.klutter,
                serviceDTO = dto
            ).generate()

            val generated = project.flutter.file.resolve("generated")
            if(!generated.exists()) generated.mkdirs()

            val javalib = project.android.app().resolve("src/main/java/dev/buijs/klutter/generated")
            if(!javalib.exists()) javalib.mkdirs()

            val command = """
                    flutter pub add pigeon
                    flutter pub run pigeon \
                    --input ${project.klutter.file}/.klutter/pigeon.dart \
                    --dart_out ${generated}/adapter.dart \
                    --java_out $javalib/Adapter.java \
                    --java_package "dev.buijs.klutter.generated" \
                    --objc_header_out ios/Runner/pigeon.h \
                    --objc_source_out ios/Runner/pigeon.m
                    flutter pub remove pigeon
                """"
            val pigeonlogging = command.runCommand(project.root.folder)
            logger.info(pigeonlogging?:"")
        }
    }
}