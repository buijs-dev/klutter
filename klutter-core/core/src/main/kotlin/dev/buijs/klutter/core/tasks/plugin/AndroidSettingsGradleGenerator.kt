package dev.buijs.klutter.core.tasks.plugin

import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.KlutterPrinter
import dev.buijs.klutter.core.KlutterWriter
import dev.buijs.klutter.core.tasks.shared.DefaultWriter
import java.io.File

/**
 * @author Gillian Buijs
 */
internal class AndroidSettingsGradleGenerator(
    private val path: File,
): KlutterFileGenerator() {

    override fun printer() = AndroidSettingsGradlePrinter()

    override fun writer() = DefaultWriter(path, printer().print())

}

internal class AndroidSettingsGradlePrinter: KlutterPrinter {

    override fun print() = """
            |include(":platform")
            |project(":platform").projectDir = file("../platform")
            |""".trimMargin()

}