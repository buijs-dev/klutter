package dev.buijs.klutter.plugins.gradle.utils

import dev.buijs.klutter.core.FileContent
import dev.buijs.klutter.core.KlutterLogger
import dev.buijs.klutter.plugins.gradle.tasks.adapter.platform.IosPodspecVisitor
import org.gradle.api.logging.Logging
import java.io.File

/**
 * @author Gillian Buijs
 */
class AnnotatedSourceCollector(
    private val source: File,
    private val annotationName: String,
){

    private val log = Logging.getLogger(AnnotatedSourceCollector::class.java)

    fun collect(): SourceCollectorResponse {

        val annotation = if(annotationName.startsWith("@")) {
            annotationName
        } else "@$annotationName"

        val collection = search(source)
            .filter { it.content.contains(annotation) }
            .also { if(it.isEmpty()) {
                log.warn("None of the files contain '$annotation' annotation.")
            }
        }

        return SourceCollectorResponse(collection)

    }

    private fun search(directory: File): List<FileContent> {
        val classes = mutableListOf<FileContent>()

        log.debug("Scanning for files in directory '$directory'")
        if (directory.exists()) {
            directory.walkTopDown().forEach { f ->
                if(f.isFile) {
                    log.debug("Found file '$f' in directory '$directory'")
                    classes.add(FileContent(file = f, content = f.readText()))
                }
            }
        } else log.error("Failed to scan directory because it does not exist: '$directory'")

        return classes
    }
}

/**
 * @author Gillian Buijs
 */
data class SourceCollectorResponse(
    val collection: List<FileContent>
)