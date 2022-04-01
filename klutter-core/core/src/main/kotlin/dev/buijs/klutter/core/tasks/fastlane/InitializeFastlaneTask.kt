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


package dev.buijs.klutter.core.tasks.fastlane

import dev.buijs.klutter.core.Android
import dev.buijs.klutter.core.IOS
import dev.buijs.klutter.core.KlutterInternalException
import dev.buijs.klutter.core.KlutterTask
import dev.buijs.klutter.core.tasks.adapter.flutter.AndroidManifestReader
import java.io.File
import java.io.InputStream
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

/**
 * @author Gillian Buijs
 */
class InitializeFastlaneTask(
    private val android: Android,
    private val ios: IOS,
    private var appId: String? = null,
)
    : KlutterTask
{

    override fun run() {

        //Use the given appId or retrieve it from the android manifest xml file
        appId = appId ?: AndroidManifestReader.deserialize(android.manifest().readText()).applicationId

        initAndroid()
        //TODO initIOS()

    }

    private fun initAndroid() {
        //Load the zip file with the fastlane android folder template or throw KIE if not possible
        val fastlaneFolder = InitializeFastlaneTask::class.java.classLoader.getResourceAsStream("fastlane_android.zip")
            ?: throw KlutterInternalException("Could not locate template for android fastlane.")

        //Copy the unzipped template to the given folder or throw KIE if failed
        if(!copy(android.fastlane(), fastlaneFolder)){
            throw KlutterInternalException("Failed to create fastlane folder in ./android.")
        }

        //Load the zip file with the google_keys.json and Gemfile or throw KIE if not possible
        val fastlaneFiles = InitializeFastlaneTask::class.java.classLoader.getResourceAsStream("fastlane_android_files.zip")
            ?: throw KlutterInternalException("Could not locate template for google_keys.json")

        //Copy the files in the resource or throw KIE if failed
        if(!copy(android.file, fastlaneFiles)){
            throw KlutterInternalException("Failed to create Gemfile and/or google_json.keys in ./android. ")
        }
    }


    /**
     * Extract the zip file and write the content to the project location.
     */
    private fun copy(root: File, input: InputStream): Boolean {

        //Create the fastlane folder
        root.toPath().createDirectories().also {
            if(!it.exists()) return false
        }

        //Convert the inputstream
        val zis = ZipInputStream(input)

        //Get the first entry (file or folder)
        var entry = zis.nextEntry

        //Process the entry or stop processing if it's null
        while (entry != null) {
            entry.copy(root, zis)
            entry = zis.nextEntry
        }

        zis.close()

        //Processing was a succcess, spread the word!
        return true

    }

    /**
     * Copy a file or folder entry to the project location.
     */
    private fun ZipEntry.copy(root: File, zis: ZipInputStream) {

        when {

            name.contains("DS_Store") || name.contains("MACOS") -> {
                //Do nothing because this does not have to copied
            }

            isDirectory -> {
                //Create a directory if it does not yet exist
                maybeCreateDirectory("$root/$name")
            }

            else -> {
                //Create a file and write the content
                createFile(File("$root/$name"), zis)
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
            if(!path.exists()) path.createDirectories()
        }

    /**
     * Create a new file and write the content of the ZipInputStream instance to it.
     * Replace KLUTTER_APP_ID tag if present in the content.
     */
    private fun createFile(file: File, content: ZipInputStream) {
        val text = String(content.readAllBytes()).maybeReplace("KLUTTER_APP_ID", appId!!)
        file.createNewFile().also { done ->
            if(done) {
                file.writeText(text)
                file.setWritable(true)
                file.setReadable(true)
                file.setExecutable(true)
            }
        }

    }


}