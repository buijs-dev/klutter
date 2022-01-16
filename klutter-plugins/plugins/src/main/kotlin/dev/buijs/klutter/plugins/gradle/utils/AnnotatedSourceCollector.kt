package dev.buijs.klutter.plugins.gradle.utils

import dev.buijs.klutter.core.FileContent
import dev.buijs.klutter.core.KlutterLogger
import java.io.File

/**
 * @author Gillian Buijs
 */
class AnnotatedSourceCollector(
    private val source: File,
    private val annotationName: String
    ){

    val logger = KlutterLogger()

    fun collect(): SourceCollectorResponse {

        val annotation = if(annotationName.startsWith("@")) {
            annotationName
        } else "@$annotationName"

        val collection = search(source)
            .filter { it.content.contains(annotation) }
            .also { if(it.isEmpty()) {
                logger.warn("None of the files contain '$annotation' annotation.")
            }
        }

        return SourceCollectorResponse(logger, collection)

    }

    private fun search(directory: File): List<FileContent> {
        val classes = mutableListOf<FileContent>()

        logger.debug("Scanning for files in directory '$directory'")
        if (directory.exists()) {
            directory.walkTopDown().forEach { f ->
                if(f.isFile) {
                    logger.debug("Found file '$f' in directory '$directory'")
                    classes.add(FileContent(file = f, content = f.readText()))
                }
            }
        } else logger.error("Failed to scan directory because it does not exist: '$directory'")

        return classes
    }
}

/**
 * @author Gillian Buijs
 */
data class SourceCollectorResponse(
    val logger: KlutterLogger,
    val collection: List<FileContent>
)