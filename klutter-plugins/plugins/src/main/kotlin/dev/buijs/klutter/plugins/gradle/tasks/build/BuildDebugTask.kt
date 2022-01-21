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


package dev.buijs.klutter.plugins.gradle.tasks.build

import dev.buijs.klutter.core.KlutterGradleException
import dev.buijs.klutter.core.KlutterLogger
import dev.buijs.klutter.plugins.gradle.KlutterTask
import dev.buijs.klutter.plugins.gradle.utils.runCommand
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.internal.logging.text.StyledTextOutputFactory
import javax.inject.Inject

/**
 * @author Gillian Buijs
 */
open class BuildDebugTask
@Inject constructor(styledTextOutputFactory: StyledTextOutputFactory):
    KlutterTask(styledTextOutputFactory)
{
    private val log = Logging.getLogger(BuildDebugTask::class.java)

    override fun describe() {

        log.info("Clean project")

        val root = project().root.folder
        val aarFile = root.resolve("./klutter/kmp.aar").absoluteFile
        if(aarFile.exists()){
            log.debug("Deleting file: $aarFile")
            aarFile.delete()
        } else log.debug("File does not exist, continuing: $aarFile")

        log.info("Build KMP module")
        val kmp = project().kmp.file.also { kmpRoot ->
            listOf(
                "gradlew",
                "gradlew.bat",
                "gradle",
                "gradle/wrapper/gradle-wrapper.jar",
                "gradle/wrapper/gradle-wrapper.properties"
            ).forEach {
                kmpRoot.resolve(it).also {  file ->
                    if(!file.exists()) {
                        throw KlutterGradleException("Missing Gradle wrapper file: '$file'")
                    }
                }
            }

            log.debug("Running ./gradlew clean build in directory '$kmpRoot'")
        }

        log.debug("./gradlew clean build".runCommand(kmp)
            ?:throw KlutterGradleException("Oops Gradle build/clean did not seem to do work..."))

        val artifact = kmp.resolve("${project().kmp.build()}/outputs/aar/${project().kmp.moduleName()}-debug.aar")

        if(!artifact.exists()) {
            throw KlutterGradleException(
                "Artifact not found in directory '$artifact'".withStacktrace(logger)
            )
        }

        val klutterBuildDir = root.resolve(".klutter").also {
            if(!it.exists()) { it.mkdir() }
        }

        val target =  klutterBuildDir.resolve("kmp.aar").also {
            if(it.exists()) { it.delete() }
        }

        log.info("Copy Android KMP .aar file to root .klutter directory")
        artifact.copyTo(target)
        log.info("Update Flutter dependencies")
        log.debug("""flutter pub get""".runCommand(root)?:"")
        log.info("Remove Flutter iOS pods")
        root.resolve("ios/Pods").also { if(it.exists()){ it.delete() } }
        root.resolve("ios/Podfile.lock").also { if(it.exists()){ it.delete() } }
        log.info("Pod Update")
        log.debug("""pod update""".runCommand(project().ios.file)?:"")
        log.info("Klutter Build finished")
    }

    private fun String.withStacktrace(logger: KlutterLogger) =
        "$this\r\n\r\nCaused by:\r\n\r\n${logger.messages().joinToString{ "${it.message}\r\n" }}"

}

