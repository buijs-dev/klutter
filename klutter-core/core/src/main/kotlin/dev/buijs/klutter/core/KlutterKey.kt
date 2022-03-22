@file:JvmName("Klutter")
package dev.buijs.klutter.core

import org.gradle.api.Project
import java.io.File

private const val secretname = "klutter.properties"

fun Project.key(name: String) = keys(toLocation(this))[name]

fun secrets(project: Project) = Secrets(keys(toLocation(project)))

fun secrets(location: String) = Secrets(keys(File(location)))

class Secrets(
    private val properties: HashMap<String, String>,
) {

    fun get(key: String) = properties[key]

}

internal fun keys(location: File): HashMap<String, String> {

    val secrets = location.listFiles()
        ?.firstOrNull { it.name == secretname }
        ?: throw KlutterGradleException("File klutter.properties could not be located in $location")

    val properties = HashMap<String, String>()

    secrets.forEachLine {
        val pair = it.split("=")
        if(pair.size == 2){
            properties[pair[0]] = pair[1]
        }
    }

   return properties

}

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