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


package dev.buijs.klutter.core.tasks.project

import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.tasks.adapter.flutter.AndroidBuildGradleGenerator
import dev.buijs.klutter.core.tasks.adapter.flutter.AndroidRootBuildGradleGenerator
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.name

private const val klutterVersion = "2022-pre-alpha-5"
private const val klutterCoreVersion = "core-0.10.11"

/**
 * Task to create a new Klutter project. This task is only available through the CLI distribution.
 *
 * <Note about the CLI dist:</b>
 * The default Klutter template contains its own CLI distribution which places the executables in the root project
 * and the lib folder in buildSrc/klutter-cli. This makes it easier to use by enabling to run./klutter from the root.
 * This task copies the lib folder from the calling CLI distribution to avoid downloading the CLI distribution twice.
 *
 * [cliDistributionLocation] Absolute path to klutter-cli root folder.
 * [projectName] Name of root folder containing the Klutter project.
 * [appId] ApplicationId of the app.
 *
 * @author Gillian Buijs
 */
class CreateProjectTask(
    private val projectName: String,
    private val appId: String,
    private val cliDistributionLocation: String,
    projectLocation: String,
)
    : KlutterTask
{

    private val folder = Path.of("$projectLocation/$projectName").toFile()

    override fun run() {

        //Load the zip file with the project template
        //or return Resource_NOT_FOUND if not possible
        val resource = CreateProjectTask::class.java.classLoader.getResourceAsStream("example.zip")
            ?: throw KlutterInternalException("Could not locate template for project.")

        //Copy the unzipped project template to the given folder
        //or return PROJECT_NOT_CREATED if not successful
        if(!copy(resource)){
            throw KlutterInternalException("Failed to create project...")
        }

        //Create project structure
        val project = KlutterProjectFactory.create(folder, validate = true)
            ?: throw KlutterInternalException("Project was created but some folders are missing...")

        //Generate android gradle files
        AndroidRootBuildGradleGenerator(project.android).generate()

        //Generate android/app gradle files
        AndroidBuildGradleGenerator(project.android).generate()

        //Copy the lib folder from the CLI dist to buildSrc
        File(cliDistributionLocation).resolve("lib").also { binFolder ->

            if(!binFolder.exists()) {
                throw KlutterInternalException("Failed to locate Klutter CLI lib folder in $binFolder")
            }

            //Create klutter-cli/lib folder
            val destination = project.root.resolve(".tools/klutter-cli/lib").also {
                if(!it.exists()) {
                    it.mkdirs().also { created ->
                        if(!created) {
                            throw KlutterInternalException("Failed to create .tools/klutter-cli/lib folder.")
                        }
                    }
                }
            }

            //Copy all jar files from the CLI dist folder to buildSrc/klutter-cli/lib
            binFolder.listFiles()?.forEach { file ->
                Files.copy(file.toPath(), destination.resolve(file.name).toPath())
            }

        }

    }

    /**
     * Extract the zip file and write the content to the project location.
     */
    private fun copy(input: InputStream): Boolean {

        //Create the top level project folder or return false if not successful
        folder.toPath().createDirectories().also {
            if(!it.exists()) return false
        }

        //Convert the inputstream
        val zis = ZipInputStream(input)

        //Get the first entry (file or folder)
        var entry = zis.nextEntry

        //Process the entry or stop processing if it's null
        while (entry != null) {
            entry.copy(zis)
            entry = zis.nextEntry
        }

        zis.close()

        //Processing was a succcess, spread the word!
        return true

    }

    /**
     * Copy a file or folder entry to the project location.
     */
    private fun ZipEntry.copy(zis: ZipInputStream) {
        val filename = name.maybeReplace("KLUTTER_PROJECT_NAME", projectName)

        when {

            filename.contains("DS_Store") || filename.contains("MACOS") -> {
                //Do nothing because this does not have to copied
            }

            isDirectory -> {
                //Create a directory if it does not yet exist
                maybeCreateDirectory("$folder/$filename")
            }

            else -> {
                //Create a file and write the content
                createFile(File("$folder/$filename"), zis)
            }

        }
    }

    /**
     * Replace the search term with a replacement String if found
     * or return the original String if not.
     */
    private fun String.maybeReplace(search: String, replacement: String) =
        if(contains(search)) replace(search, replacement) else this

    /**
     * Create a new directory if it does not exist or do nothing.
     */
    private fun maybeCreateDirectory(folder: String) =
        Path.of(folder).also { path ->
            if(path.name == "KLUTTER_APP_ID") {

                //Split the appId because each part of the appId should be a folder
                val splitted = appId.split(".")
                val directories = mutableListOf<Path>()

                //Created a nested folder depending on the index
                splitted.forEachIndexed { index, _ ->
                    var to = ""
                    var i = - 1
                    while(i < index) {
                        i += 1
                        to = "$to/${splitted[i]}"
                    }

                    directories.add(Path.of(path.absolutePathString().replace("KLUTTER_APP_ID", to)))

                }

                directories.forEach { it.createDirectories() }

            } else {
                if(!path.exists()) path.createDirectories()
            }

        }

    /**
     * Create a new file and write the content of the ZipInputStream instance to it.
     * Replace KLUTTER_PROJECT_NAME and KLUTTER_VERSION tags if present in the content.
     */
    private fun createFile(file: File, content: ZipInputStream) {

        //If not text based then it should be written as bytes
        if(writeBytes(file)) {
            file.createNewFile()
            file.writeBytes(content.readAllBytes())
        }

        //Other files are processed as text and might or might not contain placeholders to be replaced
        else {
            val text = String(content.readAllBytes())
                .maybeReplace("KLUTTER_PROJECT_NAME", projectName)
                .maybeReplace("KLUTTER_VERSION", klutterVersion)
                .maybeReplace("KLUTTER_CORE_VERSION", klutterCoreVersion)
                .maybeReplace("KLUTTER_APP_ID", appId)
                .maybeReplace("KLUTTER_APP_NAME", appId.substringAfterLast("."))

            File(file.path.maybeReplace("KLUTTER_APP_ID", appId.replace(".", "/"))).also {

                if(!it.parentFile.exists()) {
                    it.parentFile.mkdirs()
                }

                it.createNewFile().also { done ->
                    if(done) {
                        it.writeText(text)
                        it.setWritable(true)
                        it.setReadable(true)
                        it.setExecutable(true)
                    }
                }

            }
        }

    }

    private fun writeBytes(file: File): Boolean {
        val excludes = listOf("png", "jar", "zip", "lproj", "pbxproj")
        val ext = file.extension
        return when {
            excludes.contains(ext) -> true
            ext.startsWith("xc") -> true
            else -> false
        }
    }

}