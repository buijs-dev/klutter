package dev.buijs.klutter.core.tasks.plugin

import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.KlutterPrinter
import dev.buijs.klutter.core.KlutterWriter
import dev.buijs.klutter.core.tasks.shared.DefaultWriter
import java.io.File

/**
 * @author Gillian Buijs
 */
internal class FlutterPubspecGenerator(
    private val path: File,
    private val config: FlutterLibraryConfig,
): KlutterFileGenerator() {

    override fun printer() = FlutterPubspecPrinter(
        libraryName = config.libraryName,
        libraryDescription = config.libraryDescription,
        libraryVersion = config.libraryVersion,
        libraryHomepage = config.libraryHomepage,
        developerOrganisation = config.developerOrganisation,
        pluginClassName = config.pluginClassName,
    )

    override fun writer() = DefaultWriter(path, printer().print())

}

internal class FlutterPubspecPrinter(
    private val libraryName: String,
    private val libraryDescription: String,
    private val libraryVersion: String,
    private val libraryHomepage: String,
    private val developerOrganisation: String,
    private val pluginClassName: String,
): KlutterPrinter {

    override fun print() = """
            |name: $libraryName
            |description: $libraryDescription
            |version: $libraryVersion
            |homepage: $libraryHomepage
            |
            |environment:
            |  sdk: ">=2.16.1 <3.0.0"
            |  flutter: ">=2.5.0"
            |
            |dependencies:
            |  flutter:
            |    sdk: flutter
            |
            |flutter:
            |  plugin:
            |    platforms:
            |      android:
            |        package: $developerOrganisation.$libraryName
            |        pluginClass: $pluginClassName
            |      ios:
            |        pluginClass: $pluginClassName
            |""".trimMargin()

}