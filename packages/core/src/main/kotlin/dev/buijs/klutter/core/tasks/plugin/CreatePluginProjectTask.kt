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
package dev.buijs.klutter.core.tasks.plugin

import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.tasks.shared.ResourceZipCopyUtil
import java.nio.file.Path

/**
 * @author Gillian Buijs
 */
class CreatePluginProjectTask(
    private val libraryName: String,
    private val organisation: String,
    private val projectLocation: String,
)
    : KlutterTask
{

    override fun run() {

        //Load the zip file with the project template
        //or return Resource_NOT_FOUND if not possible
        val resource = CreatePluginProjectTask::class.java.classLoader.getResourceAsStream("plugin-template.zip")
            ?: throw KlutterInternalException("Could not locate template for plugin project.")

        //Copy the unzipped project template to the given folder
        //or return PROJECT_NOT_CREATED if not successful
        val copymachine = ResourceZipCopyUtil(
            folder = Path.of("$projectLocation/$libraryName").toFile(),
            filenameSubstitutions = mapOf(
                "DEVELOPER_ORGANISATION" to organisation,
                "LIBRARY_NAME" to libraryName,
            ),
            filecontentSubstituions = mapOf(
                "KOTLIN_VERSION" to "1.6.10",
                "ANDROID_GRADLE_VERSION" to "7.0.4",
                "KLUTTER_VERSION" to "0.11.7",
                "KOTLINX_VERSION" to "1.3.3",
                "LIBRARY_NAME" to libraryName,
                "DEVELOPER_ORGANISATION" to organisation,
            ),
        )

        if(!copymachine.copy(resource)){
            throw KlutterInternalException("Failed to create project...")
        }

    }

}