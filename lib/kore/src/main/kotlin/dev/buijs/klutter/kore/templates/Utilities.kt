package dev.buijs.klutter.kore.templates

import dev.buijs.klutter.kore.ast.*

/**
 * Return the Dart className of an [AbstractType].
 */
internal fun AbstractType.dartType() = when(this) {
    is StandardType -> this.dartType
    else -> this.className
}

/**
 * Append multiple lines.
 */
internal fun StringBuilder.appendLines(lines: Collection<String>) {
    lines.forEach { line -> this.appendLine(line) }
}

internal fun StringBuilder.appendTemplate(template: String) {
    append(template.trimMargin())
}