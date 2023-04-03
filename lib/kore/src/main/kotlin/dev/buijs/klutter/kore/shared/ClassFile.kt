package dev.buijs.klutter.kore.shared

import dev.buijs.klutter.kore.KlutterException
import java.io.File
import java.nio.file.Files

/**
 * Helper class to convert a File to byte[]
 * which the [ClassFileLoader] uses
 * to add a Class to the classpath.
 */
data class ClassFile(
    private val file: File, val className: String,
) {
    val classContent: ByteArray = file.toByteArray()
}

/**
 * Convert File to byte[].
 */
private fun File.toByteArray(): ByteArray = try {
    Files.readAllBytes(toPath())
} catch (e: Exception) {
    throw KlutterException("Unable to read File: ${e.message}")
}
