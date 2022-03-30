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

@file:JvmName("Klutter")
package dev.buijs.klutter.core

import org.gradle.api.Project
import java.io.File

private const val secretname = "klutter.properties"

/**
 * Create an instance of [Secrets] by passing a reference of a Gradle Project
 * and retrieve the value associated with the key of type String with [name].
 */
fun Project.key(name: String) = keys(toLocation(this))[name]

/**
 * Create an instance of [Secrets] by passing a reference of a Gradle Project
 * and finding the klutter.properties file in this project using the [toLocation] function.
 */
fun secrets(project: Project) = Secrets(keys(toLocation(project)))

/**
 * Create an instance of [Secrets] by passing the folder where the klutter.properties file is located.
 */
fun secrets(location: String) = Secrets(keys(File(location)))

/**
 * Wrapper class to store a key-value map of properties that are not public.
 */
class Secrets(
    private val properties: HashMap<String, String>,
) {

    fun get(key: String) = properties[key]

}

/**
 * Read a key-value properties file named [secretname]] to a hashmap.
 */
internal fun keys(location: File): HashMap<String, String> {

    val secrets = location.listFiles()
        ?.firstOrNull { it.name == secretname }
        ?: throw KlutterGradleException("File klutter.properties could not be located in $location")

    return KlutterPropertiesReader(secrets).read()

}

/**
 * Find the klutter.poperties file in default location using a Gradle project as reference.
 */
internal fun toLocation(project: Project): File {
    val rootProject = project.rootProject.rootDir
    //Android package has it's own settings file due to Flutter requirements.
    //This means that rootDir is pointing to android folder as root package
    //and not the top level when called from the android or app build.gradle.
    return if(rootProject.absolutePath.endsWith("android")){
        rootProject.resolve("..")
    } else {
        rootProject
    }
}