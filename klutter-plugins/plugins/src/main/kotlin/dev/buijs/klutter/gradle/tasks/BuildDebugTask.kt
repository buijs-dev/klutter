package dev.buijs.klutter.gradle.tasks

import dev.buijs.klutter.core.KlutterGradleException
import dev.buijs.klutter.core.KlutterLogger
import dev.buijs.klutter.gradle.utils.runCommand
import org.gradle.internal.logging.text.StyledTextOutputFactory
import java.io.File
import javax.inject.Inject

/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 */
open class BuildDebugTask
@Inject constructor(styledTextOutputFactory: StyledTextOutputFactory):
    KlutterGradleTask(styledTextOutputFactory)
{

    override fun describe() {
        logger.info("Clean project")

        val flutter = flutter()
        val aarFile = flutter.resolve("./klutter/kmp.aar").absoluteFile
        if(aarFile.exists()){
            logger.debug("Deleting file: $aarFile")
            aarFile.delete()
        } else logger.debug("File does not exist, continuing: $aarFile")

        val kmpModuleRoot = kmp().absolutePath.substringBeforeLast("/src").also {
            logger.debug("KMP root is '$it'")
        }

        val kmpModuleName = kmpModuleRoot.substringAfterLast("kmp/").also {
            logger.debug("KMP module name is '$it'")
        }

        if(kmpModuleName == "" || kmpModuleName.contains("/")){
            throw KlutterGradleException("""
                Could not determine name of KMP submodule.
                Please verify the multiplatform name is of pattern: 
                'kmp/your-sub-module-name/src/commonMain' 
            """.trimIndent(), logger.messages().joinToString("\r\n"))
        }

        logger.info("Build KMP module")
        val kmp = File(kmpModuleRoot.substringBeforeLast(kmpModuleName)).also { root ->
            listOf(
                "gradlew",
                "gradlew.bat",
                "gradle",
                "gradle/wrapper/gradle-wrapper.jar",
                "gradle/wrapper/gradle-wrapper.properties"
            ).forEach {
                root.resolve(it).also {  file ->
                    if(!file.exists()) {
                        throw KlutterGradleException("Missing Gradle wrapper file: '$file'")
                    }
                }
            }

            logger.debug("Running ./gradlew clean build in directory '$root'")
        }

        logger.debug("./gradlew clean build".runCommand(kmp)
            ?:throw KlutterGradleException("Oops Gradle build/clean did not seem to do work..."))

        val artifact = kmp.resolve("$kmpModuleRoot/build/outputs/aar/$kmpModuleName-debug.aar")

        if(!artifact.exists()) {
            throw KlutterGradleException(
                "Artifact not found in directory '$artifact'".withStacktrace(logger)
            )
        }

        val klutterBuildDir = flutter().resolve(".klutter").also {
            if(!it.exists()) { it.mkdir() }
        }

        val target =  klutterBuildDir.resolve("kmp.aar").also {
            if(it.exists()) { it.delete() }
        }

        logger.info("Copy Android KMP .aar file to root .klutter directory")
        artifact.copyTo(target)
        logger.info("Update Flutter dependencies")
        logger.debug("""flutter pub get""".runCommand(flutter)?:"")
        logger.info("Remove Flutter iOS pods")
        flutter.resolve("ios/Pods").also { if(it.exists()){ it.delete() } }
        flutter.resolve("ios/Podfile.lock").also { if(it.exists()){ it.delete() } }
        logger.info("Pod Update")
        logger.debug("""pod update""".runCommand(flutter.resolve("ios"))?:"")
        logger.info("Klutter Build finished")
    }

    private fun String.withStacktrace(logger: KlutterLogger) =
        "$this\r\n\r\nCaused by:\r\n\r\n${logger.messages().joinToString{ "${it.message}\r\n" }}"

}

