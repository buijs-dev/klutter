package dev.buijs.klutter.plugins.gradle.tasks.adapter.kmp

import dev.buijs.klutter.core.KMP
import dev.buijs.klutter.core.Root
import dev.buijs.klutter.plugins.gradle.KlutterTestProject
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import kotlin.io.path.createDirectory
import kotlin.io.path.createFile
import kotlin.io.path.writeText

/**
 * @author Gillian Buijs
 */

internal class KmpCommonMainBuildGradleVisitorTest: WordSpec({

    "Using the KmpCommonMainBuildGradleVisitor" should {

        val dollar = "$"
        val commonGradleFile = """
        apply(from = ".klutter/klutter.gradle.kts")

        plugins {
            id("com.android.library")
            kotlin("multiplatform")
            kotlin("native.cocoapods")
            kotlin("plugin.serialization") version "1.6.10"
        }

        kotlin {
            android()
            iosX64()
            iosArm64()

            cocoapods {
                summary = "Some description for the Shared Module"
                homepage = "Link to the Shared Module homepage"
                ios.deploymentTarget = "13.0"
                framework {
                    baseName = "common"
                }
            }

            val klutterAnnotationsKmpVersion = project.extra["klutterAnnotationsKmpVersion"]
            val kotlinxVersion = project.extra["kotlinxVersion"]
            val ktorVersion = project.extra["ktorVersion"]

            sourceSets {
                val commonMain by getting {
                    dependencies {
                        implementation("dev.buijs.klutter:annotations-kmp:${dollar}klutterAnnotationsKmpVersion")
                        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${dollar}kotlinxVersion")
                        implementation("io.ktor:ktor-client-core:${dollar}ktorVersion")
                        implementation("io.ktor:ktor-client-auth:${dollar}ktorVersion")
                    }
                }
                val commonTest by getting {
                    dependencies {
                        implementation(kotlin("test-common"))
                        implementation(kotlin("test-annotations-common"))
                        implementation(kotlin("test-junit"))
                        implementation("io.ktor:ktor-client-mock:${dollar}ktorVersion")
                        implementation("junit:junit:4.13.2")
                        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")

                    }
                }
                val androidMain by getting {
                    dependencies {
                        implementation("io.ktor:ktor-client-android:${dollar}ktorVersion")//NOKLUTTER
                        runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
                    }
                }
                val androidTest by getting {
                    dependencies {
                        implementation(kotlin("test-junit"))
                        implementation("junit:junit:4.13.2")
                    }
                }
                val iosX64Main by getting
                val iosArm64Main by getting
                val iosMain by creating {
                    dependsOn(commonMain)
                    iosX64Main.dependsOn(this)
                    iosArm64Main.dependsOn(this)

                    dependencies {
                        implementation("io.ktor:ktor-client-ios:${dollar}ktorVersion")
                    }
                }
                val iosX64Test by getting
                val iosArm64Test by getting
                val iosTest by creating {
                    dependsOn(commonTest)
                    iosX64Test.dependsOn(this)
                    iosArm64Test.dependsOn(this)
                }
            }
        }

        android {
            compileSdk = 31
            sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
            defaultConfig {
                minSdk = 21
                targetSdk = 31
            }
        }
    """.trimIndent()

        "Return a list of dependencies" {

            val project = KlutterTestProject()
            val kmp = KMP(Root(project.projectDir.toFile()))
            kmp.module().resolve("build.gradle.kts").also {
                it.createNewFile()
                it.writeText(commonGradleFile)
            }

            val klutterDir = project.projectDir.resolve(".klutter").also { it.createDirectory() }
            val properties = klutterDir.resolve("klutter.properties").also { it.createFile() }
            properties.writeText("""
                       app.version.code=1
                       app.version.name=1.0.0
                       app.id=dev.buijs.klutter.example.basic
                       android.sdk.minimum=21
                       android.sdk.compile=31
                       android.sdk.target=31
                       ios.version=13.0
                       flutter.sdk.version=2.5.3
                       klutter.gradle.plugin.version=0.3.39-pre-alpha
                       klutter.annotations.kmp.version=0.2.49
                       kotlin.version=1.6.10
                       ktor.version=1.3.2
                       kotlinx.version=1.3.10
                       gradle.version=7.0.4
                       flutter.sdk.location=/Users/boba/tools/flutter
            """.trimIndent())

            val sut = KmpCommonMainBuildGradleVisitor(kmp)
            sut.visit()

            val dependencies = sut.requiredDependencies

            dependencies.size shouldBe 5
            dependencies[0] shouldBe "implementation 'dev.buijs.klutter:annotations-kmp:0.2.49'"
            dependencies[1] shouldBe "implementation 'org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.10'"
            dependencies[2] shouldBe "implementation 'io.ktor:ktor-client-core:1.3.2'"
            dependencies[3] shouldBe "implementation 'io.ktor:ktor-client-auth:1.3.2'"
            dependencies[4] shouldBe "runtimeOnly 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0'"

        }

    }


})
