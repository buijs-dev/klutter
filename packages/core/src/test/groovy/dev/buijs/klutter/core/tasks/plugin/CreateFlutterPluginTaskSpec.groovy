package dev.buijs.klutter.core.tasks.plugin

import dev.buijs.klutter.core.Klutter
import spock.lang.Specification

import java.lang.reflect.Field
import java.nio.file.Files

class CreateFlutterPluginTaskSpec extends Specification {

    def "Verify creating an instance of Secrets by supplying a location"() {

        given:
        def temp = Files.createTempDirectory("buildsrc")
        def properties = temp.resolve("klutter.secrets").toFile()
        properties.createNewFile()
        properties.write(
                "store.file.uri=x\nstore.password=y\nkey.alias=me\nkey.password=pass\n"
        )

        when:
        def sut = Klutter.secrets(temp.toAbsolutePath().toFile().absolutePath)

        then:
        sut["store.file.uri"] == "x"
        sut["store.password"] == "y"
        sut["key.alias"] == "me"
        sut["key.password"] == "pass"

    }

    // "Using the Create command" should {
    //
    //        "create a klutter project folder in the given folder" {
    //
    //            //given
    //            val temp = Files.createTempDirectory("foo").toFile()
    //            val output = Files.createTempDirectory("bar").toFile()
    //            val project = KlutterTestProject(projectDir = temp.toPath())
    //
    //            project.platformDir.resolve("platform.podspec").also {
    //                it.createNewFile()
    //            }
    //
    //            project.platformDir.resolve("build.gradle.kts").also {
    //                it.createNewFile()
    //                it.writeText("""
    //                    plugins {
    //                        id("com.android.library")
    //                        id("dev.buijs.klutter.gradle")
    //                        kotlin("multiplatform")
    //                        kotlin("native.cocoapods")
    //                        kotlin("plugin.serialization") version "1.6.10"
    //                    }
    //
    //                    version = "1.0"
    //
    //                    klutter {
    //
    //                        app {
    //                            // Flutter library name
    //                            name = "example_project"
    //
    //                            // Flutter library version
    //                            version = "1.0.0"
    //
    //                        }
    //
    //                    }
    //
    //                    kotlin {
    //                        android()
    //                        iosX64()
    //                        iosArm64()
    //
    //                        cocoapods {
    //                            summary = "Some description for the Shared Module"
    //                            homepage = "Link to the Shared Module homepage"
    //                            ios.deploymentTarget = "14.1"
    //                            framework {
    //                                baseName = "platform"
    //                            }
    //                        }
    //
    //                        sourceSets {
    //
    //                            val commonMain by getting {
    //                                dependencies {
    //                                    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    //                                    api("dev.buijs.klutter:annotations-kmp:2022-pre-alpha-5")
    //                                    implementation("io.ktor:ktor-client-core:1.6.7")
    //                                    implementation("io.ktor:ktor-client-auth:1.6.7")
    //                                }
    //                            }
    //
    //                            val commonTest by getting
    //
    //                        }
    //                    }
    //
    //                    android {
    //                        compileSdk = 31
    //                        sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    //                        defaultConfig {
    //                            minSdk = 21
    //                            targetSdk = 31
    //                        }
    //                    }
    //
    //                """.trimIndent())
    //            }
    //
    //            //setup ugly mocking yuck
    //            val ktFile: KtFile = mock()
    //            val psiManager: PsiManager = mock()
    //            val context: Project = mock()
    //            whenever(context.getService(PsiManager::class.java)).thenReturn(psiManager)
    //            whenever(psiManager.findFile(any())).thenReturn(ktFile)
    //            whenever(ktFile.text).thenReturn("""
    //                package dev.buijs.klutter.activity
    //
    //                import io.flutter.plugins.GeneratedPluginRegistrant
    //                import io.flutter.embedding.engine.FlutterEngine
    //                import androidx.annotation.NonNull
    //                import dev.buijs.klutter.adapter.GeneratedKlutterAdapter
    //                import io.flutter.plugin.common.MethodChannel
    //                import io.flutter.embedding.android.FlutterActivity
    //                import dev.buijs.klutter.annotations.kmp.KlutterAdapter
    //
    //                @KlutterAdapter
    //                class MainActivity: FlutterActivity() {
    //
    //                    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
    //                        MethodChannel(flutterEngine.dartExecutor,"KLUTTER")
    //                            .setMethodCallHandler{ call, result ->
    //                                GeneratedKlutterAdapter().handleMethodCalls(call, result)
    //                            }
    //                    }
    //
    //                }
    //            """.trimIndent())
    //
    //            //and
    //            val sut = CreateFlutterPluginTask(
    //                libraryName = "example",
    //                outputLocation = output.absolutePath,
    //                organisation = "com.example",
    //                projectFolder = project.projectDir.absolutePathString(),
    //                libraryDescription = "blablalbalbalalablalbalbalbala",
    //                libraryVersion = "1.0.0",
    //                context = context,
    //                homepageLink = "https://github/some/developer/packages/plugin",
    //            )
    //
    //            //when
    //            sut.run()
    //
    //            //then
    //            output.resolve("example/android").exists() shouldBe true
    //            output.resolve("example/ios").exists() shouldBe true
    //            output.resolve("example/lib").exists() shouldBe true
    //            output.resolve("example/platform").exists() shouldBe true
    //            output.resolve("example/build.gradle.kts").exists() shouldBe true
    //
    //        }
    //
    //    }

}