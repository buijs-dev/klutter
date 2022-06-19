package dev.buijs.klutter.core.shared

import java.io.File

private val regex = """(enum class ([^{]+?)\{[^}]+})""".toRegex()

/**
 * Scan a list of Kotlin files for enumerations
 * and convert each to a DartEnum.
 *
 * @return List<DartEnum> converted from the input files.
 */
internal fun List<File>.toDartEnumList(): List<DartEnum> = this
    .map { file -> file.readText() }
    .flatMap { text -> regex.findAll(text) }
    .map { match -> match.toDartEnum() }
    .toList()

private fun MatchResult.toDartEnum(): DartEnum {
    val name = groupValueWithoutSpaces(2)
    val values = groupValues[1]
    return values.toDartEnum(name)
}