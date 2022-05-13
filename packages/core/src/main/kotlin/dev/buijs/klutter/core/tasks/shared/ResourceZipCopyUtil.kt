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
package dev.buijs.klutter.core.tasks.shared

import java.io.File
import java.io.InputStream
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

/**
 * Utility to copy a zipped resource to a destination folder.
 *
 * @author Gillian Buijs
 */
class ResourceZipCopyUtil(

    /**
     * Output folder where to copy the resource to.
     */
    private val folder: File,

    /**
     * Map of placeholders to replace in any filename.
     *
     * Any key found will be substituted for the value.
     */
    private val filenameSubstitutions: Map<String,String>,

    /**
     * Map of placeholders to replace in any file content.
     *
     * Any key found will be substituted for the value.
     */
    private val filecontentSubstituions: Map<String,String>,
) {

    /**
     * Extract the zip file and write the content to the project location.
     */
    fun copy(input: InputStream): Boolean {

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
        var filename = name

        filenameSubstitutions.forEach { (placeholder, replacement) ->
            if(filename.contains(placeholder)) {
                filename = filename.replace(placeholder, replacement)
            }
        }

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
     * Create a new directory if it does not exist or do nothing.
     */
    private fun maybeCreateDirectory(folder: String) =
        Path.of(folder).also { path ->

            var newPath = path.absolutePathString()

            filenameSubstitutions.forEach { (placeholder, replacement) ->
                if(newPath.contains(placeholder)) {
                    newPath = newPath.replace(placeholder, replacement)
                }
            }

            File(newPath).let {
                if(!it.exists()) it.mkdirs()
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
            var text = String(content.readAllBytes())

            filecontentSubstituions.forEach { (placeholder, replacement) ->
                if (text.contains(placeholder)) {
                    text = text.replace(placeholder, replacement)
                }
            }

            var newPath = file.path

            filenameSubstitutions.forEach { (placeholder, replacement) ->
                if (newPath.contains(placeholder)) {
                    newPath = newPath.replace(placeholder, replacement)
                }
            }

            File(newPath).let {

                if (!it.parentFile.exists()) {
                    it.parentFile.mkdirs()
                }

                it.createNewFile().also { done ->
                    if (done) {
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