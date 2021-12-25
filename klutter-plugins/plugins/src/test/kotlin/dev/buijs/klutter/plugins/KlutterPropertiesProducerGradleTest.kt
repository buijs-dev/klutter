package dev.buijs.klutter.plugins


import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import org.gradle.testkit.runner.GradleRunner
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Gillian Buijs
 *
 * Contact me: https://buijs.dev
 *
 */
class KlutterPropertiesProducerGradleTest : WordSpec({

    "A configured Kotlin DSL builscript" should {
        "Lead to a successful build" {
            val projectDir = Files.createTempDirectory("")
            val buildScript = projectDir.resolve("build.gradle.kts").toFile()

            val androidAppDir = projectDir.resolve("android/app").toAbsolutePath().toFile()
            androidAppDir.mkdirs()

            val flutterDir = projectDir.resolve("flutter/lib").toAbsolutePath().toFile()
            flutterDir.mkdirs()

            val moduleDir = projectDir.resolve("somemodule").toAbsolutePath().toFile()
            moduleDir.mkdirs()

            val mainDartFile = flutterDir.resolve("main.dart").absoluteFile
            mainDartFile.createNewFile()

            val sourcesDir = androidAppDir.resolve("FakeClass.kt").absoluteFile
            sourcesDir.createNewFile()

            val mainActivityDir = androidAppDir.resolve(
                Path.of("src", "main", "java", "foo", "bar", "baz", "appz").toFile())
            mainActivityDir.mkdirs()

            val mainActivity = mainActivityDir.resolve("MainActivity.kt")
            mainActivity.createNewFile()

            val podspec = projectDir.toFile().resolve("somepod.spec").absoluteFile
            podspec.createNewFile()

            val yaml = projectDir.resolve("klutter.yaml").toFile()
            yaml.createNewFile()
            yaml.writeText(
                """
                app:
                  - version:
                      - code: 2
                      - name: 1.0.1
                  - id: dev.buijs.klutter.example

                android:
                  - sdk:
                      - minimum: 21
                      - compile: 31
                      - target: 31

                ios:
                  - version: 13.0

                flutter:
                  - sdk:
                      - version: 2.5.3
                      
                appcompat:
                  - version: 1.0.2

                kotlin:
                  - version: 1.6.0

                gradle:
                  - version: 4.2.2

                junit:
                  - version: 4.3.12

                okhttp:
                  - version: 4.10.0-RC1
            """.trimIndent()
            )

            buildScript.writeText("""
                plugins {
                    id("dev.buijs.klutter.gradle")
                }

                klutter {
                    sources = listOf(File("$sourcesDir"))
                    flutter = File("${flutterDir.absolutePath}")
                    android = File("${androidAppDir.absolutePath}")
                    ios = File("")
                    podspec = File("${podspec.absolutePath}")
                    modules = listOf(File("$moduleDir"))
                }

            """.trimIndent())

            GradleRunner.create()
                .withProjectDir(projectDir.toFile())
                .withPluginClasspath()
                .withArguments("produceConfig")
                .build()

            val generatedConfigFile = moduleDir.resolve(".klutter/config.gradle.kts").toPath().toFile()

            generatedConfigFile.exists()
            generatedConfigFile.readText().filter { !it.isWhitespace() } shouldBe """
               project.extra["kotlin.version"] = "1.6.0"
               project.extra["android.sdk.compile"] = "31"
               project.extra["android.sdk.minimum"] = "21"
               project.extra["android.sdk.target"] = "31"
               project.extra["app.version.name"] = "1.0.1"
               project.extra["app.id"] = "dev.buijs.klutter.example"
               project.extra["app.version.code"] = "2"
               project.extra["gradle.version"] = "4.2.2"
               project.extra["okhttp.version"] = "4.10.0-RC1"
               project.extra["ios.version"] = "13.0"
               project.extra["appcompat.version"] = "1.0.2"
               project.extra["junit.version"] = "4.3.12"
               project.extra["flutter.sdk.version"] = "2.5.3"
            """.filter { !it.isWhitespace() }

            //cleanup
            flutterDir.deleteRecursively()
            androidAppDir.deleteRecursively()
            Path.of("").resolve("android").toFile().delete()
            Path.of("").resolve("flutter").toFile().delete()

        }
    }

})