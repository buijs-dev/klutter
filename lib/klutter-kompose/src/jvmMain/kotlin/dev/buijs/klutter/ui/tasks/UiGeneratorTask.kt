/* Copyright (c) 2021 - 2022 Buijs Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package dev.buijs.klutter.ui.tasks

import dev.buijs.klutter.core.KlutterException
import dev.buijs.klutter.core.KlutterTask
import dev.buijs.klutter.ui.*
import dev.buijs.klutter.ui.builder.ClassFileLoader
import dev.buijs.klutter.ui.builder.UIBuilderCollector
import dev.buijs.klutter.ui.templates.AppTemplate
import dev.buijs.klutter.ui.templates.MainTemplate
import dev.buijs.klutter.ui.templates.NavigatorTemplate
import mu.KotlinLogging
import java.io.File

private val log = KotlinLogging.logger { }

/**
 * Task to generate a Flutter UI.
 *
 * Views can be constructed using a Klutter Kompose DSL, being one of:
 * - JetlagUI
 * - KlutterUI
 * - NotSwiftUI
 *
 * KlutterUI is the default DSL which resembles Flutter the most.
 * JetlagUI is a DSL which is inspired by Jetpack Compose.
 * NotSwiftUI is a DSL which is inspired by SwiftUI.
 *
 * Each DSL returns the same set of Kompose objects which are used to generate the
 * Flutter UI.
 */
class UiGeneratorTask(
    private val pathToBuild: File,
    private val pathToOutput: File,
) : KlutterTask {

    override fun run() {

        if (!pathToBuild.exists()) {
            throw KlutterException("Missing build output directory: $pathToBuild.")
        }

        if(pathToBuild.absolutePath.endsWith("jar")) {
            log.info { "Creating new ClassFileLoader from JAR: '$pathToBuild'" }
            ClassFileLoader.loadJar(pathToBuild.absolutePath)
        } else {
            val classy = pathToClasses(pathToBuild.absolutePath)
            log.info { "Creating new ClassFileLoader from classes folder: '$classy'" }
            ClassFileLoader.loadBuild(classy)
        }

        val builders = UIBuilderCollector().collect().ifEmpty {
            throw failure("No UIBuilders found!")
        }

        if (!pathToOutput.exists()) {
            throw failure("Missing output directory: $pathToOutput.")
        }

        val routes = mutableListOf<KomposeRoute>()

        for(builder in builders) {
            val className = builder.name()
            val filename = className.lowercase()
            val file = pathToOutput.resolve("${filename}.dart")
            file.createNewFile()
            file.writeText(builder.print())
            routes.add(
                KomposeRoute(
                    name = filename,
                    className = className,
                    hasNoArgConstructor = true,
                )
            )
        }

        val navigator = pathToOutput.resolve("kompose_navigator.dart")
        navigator.createNewFile()
        navigator.writeText(NavigatorTemplate(routes.distinctAndValidate()).print())

        val main = pathToOutput.resolve("main.dart")
        main.createNewFile()
        main.writeText(MainTemplate().print())

        val app = pathToOutput.resolve("kompose_app.dart")
        app.createNewFile()
        //TODO read the KomposeApp from a Kotlin DSL class
        app.writeText(AppTemplate(KomposeApp()).print())

    }

}

private fun failure(msg: String): KlutterException = KlutterException(
    """|$msg
           |
           |Make sure the root path is configured correctly in the Klutter Gradle plugin.
           |Make sure a build is executed before running this task.
        """.trimMargin()
)

private fun pathToClasses(pathToBuildFolder: String): File {
    log.info("UIBuilderCollector pathToBuilderFolder: {}", pathToBuildFolder)
    val pathToClasses = File(pathToBuildFolder).resolve("classes")
    log.info("UIBuilderCollector pathToClasses: {}", pathToClasses)
    return pathToClasses
}

private fun List<KomposeRoute>.distinctAndValidate(): List<KomposeRoute> {
    val uniqueRoutes = distinct()

    val duplicateRoutes = distinct()
        .map { it.name }
        .groupingBy { it }
        .eachCount()
        .filter { it.value > 1 }
        .map { it.key }
        .toList()

    if(duplicateRoutes.isNotEmpty()) {
        log.info { "Found duplicate routes: ${duplicateRoutes.joinToString {"$it, " }}" }
        throw KlutterException(
            "Processing routes failed 1 or more route names point to multiple screens"
        )
    }

    return uniqueRoutes
}