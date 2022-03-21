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

package dev.buijs.klutter.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import dev.buijs.klutter.core.KlutterProjectFactory
import dev.buijs.klutter.core.Root
import dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter.AndroidBuildGradleGenerator
import java.io.File
import java.io.InputStream
import java.nio.file.Path
import java.util.zip.ZipInputStream
import kotlin.io.path.absolute
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import dev.buijs.klutter.plugins.gradle.tasks.adapter.flutter.AndroidRootBuildGradleGenerator
import java.net.URL

/**
 * Utility to generate a new Klutter project.
 *
 * @author Gillian Buijs
 */
private const val klutterVersion = "2022-pre-alpha-4"

fun main(args: Array<String>) = ProjectGenerator().main(args)

class ProjectGenerator : CliktCommand() {

    private val name: String by option(help="Name of the project").default("example")
    private val location: String by option(help="Location of the project").default("./../../")

    override fun run() {

        echo("Creating Klutter project...")

        val resource = getResource()

        if(resource == null) {
            echo("Could not locate template for project")
            return
        }

        val folder = Path.of("$location/$name").absolute().toFile()
        if(folder.exists() && folder.listFiles()?.isNotEmpty() == true) {
            echo("Folder with $location/$name already exists and is not empty.")
            return
        }

        if(!copy(resource, folder, name)){
            echo("Failed to create project...")
        }

        //Create project structure
        val root = Root(folder)
        val project = KlutterProjectFactory().fromRoot(root)

        //Generate android gradle files
        AndroidRootBuildGradleGenerator(project.android).generate()

        //Generate android/app gradle files
        AndroidBuildGradleGenerator(project.android).generate()

    }

}

internal fun getResource(): InputStream? =
    URL("https://github.com/buijs-dev/klutter/blob/main/klutter-cli/example.zip").openStream()

internal fun copy(
    input: InputStream,
    root: File,
    name: String,
): Boolean {

    root.toPath().createDirectories().also { folder ->
        if(!folder.exists()) {
            return false
        }
    }

    val zis = ZipInputStream(input)
    var entry = zis.nextEntry

    while (entry != null) {

        val filename = if(entry.name.contains("KLUTTER_APP_NAME"))
            entry.name.replace("KLUTTER_APP_NAME", name)
        else entry.name

        when {
            filename.contains("DS_Store") -> {  }
            entry.isDirectory -> {
                Path.of("$root/$filename").also { path ->
                    if(!path.exists()) path.createDirectories()
                }
            }

            else -> {

                File("$root/$filename").also { file ->
                    file.createNewFile()
                    file.writeBytes(zis.readAllBytes())

                    var written = file.readText()

                    if(written.contains("KLUTTER_APP_NAME")){
                        written = written.replace("KLUTTER_APP_NAME", name)
                        file.writeText(written)
                    }

                    if(written.contains("KLUTTER_VERSION")){
                        written = written.replace("KLUTTER_VERSION", klutterVersion)
                        file.writeText(written)
                    }

                    file.setWritable(true)
                    file.setReadable(true)
                    file.setExecutable(true)

                }
            }
        }

        entry = zis.nextEntry
    }

    return true
}
