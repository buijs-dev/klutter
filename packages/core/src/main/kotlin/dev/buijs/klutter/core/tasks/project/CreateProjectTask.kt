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
import dev.buijs.klutter.core.tasks.shared.ResourceZipCopyUtil
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

private const val klutterVersion = "2022-alpha-1"
private const val klutterCoreVersion = "core-0.10.20"

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
        val copymachine = ResourceZipCopyUtil(
            folder = folder,
            filenameSubstitutions = mapOf(
                "KLUTTER_PROJECT_NAME" to projectName,
                "KLUTTER_APP_ID" to appId.replace(".", "/"),
            ),
            filecontentSubstituions = mapOf(
                "KLUTTER_PROJECT_NAME" to projectName,
                "KLUTTER_VERSION" to klutterVersion,
                "KLUTTER_CORE_VERSION" to klutterCoreVersion,
                "KLUTTER_APP_ID" to appId,
                "KLUTTER_APP_NAME" to appId.substringAfterLast(".")
            ),
        )

        if(!copymachine.copy(resource)){
            throw KlutterInternalException("Failed to create project...")
        }

        //Create project structure
        val project = KlutterProjectFactory.create(folder, validate = true)
            ?: throw KlutterInternalException("Project was created but some folders are missing...")

        //Generate android gradle files
        AndroidRootBuildGradleGenerator(project.android).generate()

        //Generate android/app gradle files
        AndroidBuildGradleGenerator(android = project.android).generate()

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

}