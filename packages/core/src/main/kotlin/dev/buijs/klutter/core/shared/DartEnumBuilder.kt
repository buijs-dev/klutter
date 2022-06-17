package dev.buijs.klutter.core.shared

import dev.buijs.klutter.core.KlutterException
import java.io.File

private val enumRegex = """(enum class ([^{]+?)\{[^}]+})""".toRegex()

internal fun List<File>.toDartEnums(): List<DartEnum> {
    return this.map { file ->
        enumRegex.findAll(file.readText())
            .map { match ->
                val name = match.groups[2]?.value?.filter { !it.isWhitespace() }
                    ?: throw KlutterException("Failed to process an enum class.")
                match.value.toDartEnums(name)
            }.toList()
    }.flatten()
}