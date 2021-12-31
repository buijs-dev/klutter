package dev.buijs.klutter.core.flutter

import dev.buijs.klutter.core.KlutterCodeGenerationException
import dev.buijs.klutter.core.KlutterCodeGenerator
import dev.buijs.klutter.core.KlutterLogger
import dev.buijs.klutter.core.KlutterPropertiesReader
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
        val properties = KlutterPropertiesReader(root.resolve(".klutter/klutter.properties").toAbsolutePath().toFile()).read()

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
        val writer = AndroidBuildGradleWriter()

        AndroidBuildGradlePrinter(properties, aarFile, android).print().also { content ->
            logger.debug("Created content for build.gradle file: \r\n $content")
            writer.write(gradle, content)
            logger.info("Written content to build.gradle file in directory '$gradle'")
        }

        AndroidBuildRootGradlePrinter(properties).print().also { content ->
            logger.debug("Created content for root build.gradle file: \r\n $content")
            val file = android.resolve("..").normalize().resolve("build.gradle")
            if(!file.exists()) {
                logger.error("Build.gradle file in android folder not found. Creating a new file!")
                file.createNewFile()
            }
            writer.write(file, content)
            logger.info("Written content to root build.gradle file in directory '$gradle'")
        }

        return logger
    }


}