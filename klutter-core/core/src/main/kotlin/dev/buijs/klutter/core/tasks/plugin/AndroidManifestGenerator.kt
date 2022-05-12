package dev.buijs.klutter.core.tasks.plugin

import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.KlutterPrinter
import dev.buijs.klutter.core.tasks.shared.DefaultWriter
import java.io.File

/**
 * @author Gillian Buijs
 */
internal class AndroidManifestGenerator(
    private val path: File,
    private val libraryName: String,
    private val developerOrganisation: String,
): KlutterFileGenerator() {

    override fun printer() = AndroidManifestPrinter(
        libraryName = libraryName,
        developerOrganisation = developerOrganisation,
    )

    override fun writer() = DefaultWriter(path, printer().print())

}

internal class AndroidManifestPrinter(
    private val libraryName: String,
    private val developerOrganisation: String,
): KlutterPrinter {

    override fun print() = """
            |<manifest xmlns:android="http://schemas.android.com/apk/res/android"
            |  package="$developerOrganisation.$libraryName">
            |</manifest>
            |""".trimMargin()

}