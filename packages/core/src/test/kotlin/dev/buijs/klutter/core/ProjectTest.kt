package dev.buijs.klutter.core

import io.kotlintest.specs.WordSpec
import java.io.File
import java.nio.file.Files

class ProjectTest: WordSpec({

    val pluginName = "ridiculous_plugin"
    val folder = Files.createTempDirectory("").toFile()
    val root = folder.absoluteFile
    val pubspec = folder.resolve("pubspec.yaml")
    pubspec.createNewFile()
    pubspec.writeText(
        """
                  name: $pluginName
                  description: this is my app
                  version: 1.0.0+1
            """.trimIndent()
    )

    "When creating a Project without a plugin name" should {

        File("$root/ios").mkdirs()
        File("$root/ios/${pluginName}.podspec").createNewFile()
        File("$root/platform").mkdirs()
        File("$root/platform/${pluginName}.podspec").createNewFile()

        "The File constructor should lookup plugin name from the pubspec.yaml" {
            val project = folder.klutterProject(null)
            project.ios.podspec().absolutePath.endsWith("ridiculous_plugin.podspec")
            project.platform.podspec().absolutePath.endsWith("ridiculous_plugin.podspec")
        }

        "The String constructor should lookup plugin name from the pubspec.yaml" {
            val project = folder.absolutePath.klutterProject(null)
            project.ios.podspec().absolutePath.endsWith("ridiculous_plugin.podspec")
            project.platform.podspec().absolutePath.endsWith("ridiculous_plugin.podspec")
        }

        "The Root constructor should lookup plugin name from the pubspec.yaml" {
            val project = Root(folder).klutterProject(null)
            project.ios.podspec().absolutePath.endsWith("ridiculous_plugin.podspec")
            project.platform.podspec().absolutePath.endsWith("ridiculous_plugin.podspec")
        }
    }

    "When creating a Project with a plugin name" should {

        pubspec.writeText(
            """
                  name: blablabla
                  description: this is my app
                  version: 1.0.0+1
            """.trimIndent()
        )

        File("$root/ios").mkdirs()
        File("$root/ios/${pluginName}.podspec").createNewFile()
        File("$root/platform").mkdirs()
        File("$root/platform/${pluginName}.podspec").createNewFile()

        "The File constructor should use the given name" {
            val project = folder.klutterProject(pluginName)
            project.ios.podspec().absolutePath.endsWith("ridiculous_plugin.podspec")
            project.platform.podspec().absolutePath.endsWith("ridiculous_plugin.podspec")
        }

        "The String constructor should use the given name" {
            val project = folder.absolutePath.klutterProject(pluginName)
            project.ios.podspec().absolutePath.endsWith("ridiculous_plugin.podspec")
            project.platform.podspec().absolutePath.endsWith("ridiculous_plugin.podspec")
        }

        "The Root constructor should use the given name" {
            val project = Root(folder).klutterProject(pluginName)
            project.ios.podspec().absolutePath.endsWith("ridiculous_plugin.podspec")
            project.platform.podspec().absolutePath.endsWith("ridiculous_plugin.podspec")
        }
    }


})