package dev.buijs.klutter.core.flutter

import dev.buijs.klutter.core.KlutterCodeGenerationException
import dev.buijs.klutter.core.KlutterCodeGenerator
import dev.buijs.klutter.core.log.KlutterLogger
import dev.buijs.klutter.core.utils.readPropertiesFromFile
import java.io.File
import java.nio.file.Path

/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 */
class AndroidBuildGradleGenerator(
    private val root: Path,
    private val android: File,
): KlutterCodeGenerator {

    private var logger = KlutterLogger()

    override fun generate(): KlutterLogger {
        val properties = readPropertiesFromFile(root.resolve(".klutter/klutter.properties").toAbsolutePath().toFile())

        if(!android.exists()){
            throw KlutterCodeGenerationException("Android directory does not exist: ${android.absolutePath}")
        }

        val gradle = android.resolve("build.gradle")
        if(gradle.exists()){
            logger.info("Deleted build.gradle file in directory '$gradle'")
            gradle.delete()
        }
        gradle.createNewFile()
        logger.info("Created build.gradle file in directory '$gradle'")

        val aarFile = root.resolve(".klutter/kmp.aar").toAbsolutePath().toFile()
        val content = AndroidBuildGradlePrinter(properties, aarFile, android).print()
        logger.debug("Created content for build.gradle file: \r\n $content")
        AndroidBuildGradleWriter().write(gradle, content)
        logger.info("Written content to build.gradle file in directory '$gradle'")
        return logger
    }


}