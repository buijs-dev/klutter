package dev.buijs.klutter.plugins


import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import org.gradle.testkit.runner.GradleRunner
import java.nio.file.Path

/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 */
class KlutterConfigProducerGradleTest : WordSpec({

    "A configured Kotlin DSL builscript" should {
        "Lead to a successful build" {
            val project = KlutterTestProject()
            val projectDir = project.projectDir
            val buildScript = project.buildGradle
            val androidAppDir = project.androidAppDir
            val flutterDir = project.flutterDir
            val klutterDir = project.klutterDir
            val moduleDir = projectDir.resolve("somemodule")
                .toAbsolutePath()
                .toFile()
                .also { it.mkdir() }

            val sourcesDir = androidAppDir.resolve("FakeClass.kt").absoluteFile
            sourcesDir.createNewFile()

            val mainActivityDir = androidAppDir.resolve(
                Path.of("src", "main", "java", "foo", "bar", "baz", "appz").toFile())
            mainActivityDir.mkdirs()

            val mainActivity = mainActivityDir.resolve("MainActivity.kt")
            mainActivity.createNewFile()

            val podspec = projectDir.toFile().resolve("somepod.spec").absoluteFile
            podspec.createNewFile()

            klutterDir.resolve("klutter.yaml")
                .also { it.createNewFile() }
                .also { it.writeText(
                    """
                    app:
                      - version:
                          - code: 1
                          - name: "1.0.0"
                      - id: "dev.buijs.klutter.example.basic"
                    
                    android:
                      - sdk:
                          - minimum: 21
                          - compile: 31
                          - target: 31
                    
                    ios:
                      - version: "13.0"
                    
                    flutter:
                      - sdk:
                          - version: "2.5.3"
                          - distributionUrl: "https://storage.googleapis.com/flutter_infra_release/releases/stable/macos/flutter_macos_2.5.3-stable.zip"
                    
                    kotlin:
                      - version: "1.6.0"
                    
                    gradle:
                      - version: "7.0.4"
                      - dependencyUpdates: "0.36.0"
                    
                    junit:
                      - version: "4.3.12"
                    
                    okhttp:
                      - version: "4.10.0-RC1"
            """.trimIndent())}

            buildScript.writeText("""
                plugins {
                    id("dev.buijs.klutter.gradle")
                }

                klutter {
                    sources = listOf(File("$sourcesDir"))
                    flutter = File("${flutterDir.absolutePath}")
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
                val appVersionCode: Int by project.extra { 1 }
                val appVersionName: String by project.extra { "1.0.0" }
                val appId: String by project.extra { "dev.buijs.klutter.example.basic" }
                val androidSdkMinimum: Int by project.extra { 21 }
                val androidSdkCompile: Int by project.extra { 31 }
                val androidSdkTarget: Int by project.extra { 31 }
                val iosVersion: String by project.extra { "13.0" }
                val flutterSdkVersion: String by project.extra { "2.5.3" }
                val flutterSdkDistributionUrl: String by project.extra { "https://storage.googleapis.com/flutter_infra_release/releases/stable/macos/flutter_macos_2.5.3-stable.zip" }
                val kotlinVersion: String by project.extra { "1.6.0" }
                val gradleVersion: String by project.extra { "7.0.4" }
                val gradleDependencyUpdates: String by project.extra { "0.36.0" }
                val junitVersion: String by project.extra { "4.3.12" }
                val okhttpVersion: String by project.extra { "4.10.0-RC1" }
                """.filter { !it.isWhitespace() }
        }
    }


})