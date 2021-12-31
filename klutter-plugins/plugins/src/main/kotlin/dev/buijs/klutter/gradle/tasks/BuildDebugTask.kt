package dev.buijs.klutter.gradle.tasks

import dev.buijs.klutter.core.KlutterGradleException
import dev.buijs.klutter.gradle.utils.runCommand
import org.gradle.internal.logging.text.StyledTextOutputFactory
import java.io.File
import javax.inject.Inject
import kotlin.io.path.deleteIfExists

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
        val aarFile = flutter.resolve("kmp.aar").absoluteFile
        if(aarFile.exists()){
            aarFile.delete()
        }

        val buildDir = flutter.resolve("build").toPath()
        buildDir.deleteIfExists()

        val kmpRoot = kmp().absolutePath.substringBeforeLast("/src")
        val kmpModuleName = kmpRoot.substringAfterLast("kmp/")

        if(kmpModuleName == "" || kmpModuleName.contains("/")){
            throw KlutterGradleException("""
                Could not determine name of KMP submodule.
                Please verify the multiplatform name is of pattern: 
                'kmp/your-sub-module-name/src/commonMain' 
            """.trimIndent())
        }

        val kmp = File(kmpRoot.substringBeforeLast(kmpModuleName))

        logger.info("Build KMP module")
        logger.debug("""./gradlew clean build"""".runCommand(kmp)?:"")

        val buildAarFile = kmp.resolve("$kmpModuleName/build/outputs/aar/$kmpModuleName-debug.aar")

        if(!buildAarFile.exists()) {
            throw KlutterGradleException("Artifact not found in directory '$buildAarFile'")
        }

        val rootBuildAarFileDirectory = flutter().resolve(".klutter").also {
            if(!it.exists()) { it.mkdir() }
        }

        val kmpAarFile =  rootBuildAarFileDirectory.resolve("kmp.aar").also {
            if(it.exists()) { it.delete() }
        }

        logger.info("Copy Android KMP .aar file to root .klutter directory")
        buildAarFile.copyTo(kmpAarFile)
        logger.info("Update Flutter dependencies")
        logger.debug("""flutter pub get""".runCommand(flutter)?:"")
        logger.info("Remove Flutter iOS pods")
        flutter.resolve("ios/Pods").also { if(it.exists()){ it.delete() } }
        flutter.resolve("ios/Podfile.lock").also { if(it.exists()){ it.delete() } }
        logger.info("Pod Update")
        logger.debug("""pod update""".runCommand(flutter.resolve("ios"))?:"")
        logger.info("Klutter Build finished")
    }

}

