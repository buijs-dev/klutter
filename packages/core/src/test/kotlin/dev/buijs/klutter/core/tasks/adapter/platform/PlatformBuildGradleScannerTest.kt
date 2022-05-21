package dev.buijs.klutter.core.tasks.adapter.platform

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import java.nio.file.Files

/**
 * @author Gillian Buijs
 */
class PlatformBuildGradleScannerTest: WordSpec({

    "using the PlatformBuildGradleScanner" should {

        val projectDir = Files.createTempDirectory("")
        val buildGradle = projectDir.resolve("build.gradle").toFile()

        buildGradle.also {
            it.createNewFile()
            it.writeText("""
                plugins {
                    id("com.android.library")
                    id("dev.buijs.klutter.gradle")
                    kotlin("multiplatform")
                    kotlin("native.cocoapods")
                    kotlin("plugin.serialization") version "1.6.10"
                }

                version = "1.0"

                klutter {

                    app {
                        
                    }

                }

                kotlin {
                    android()
                    iosX64()
                    iosArm64()

                    cocoapods {
                        summary = "Some description for the Shared Module"
                        homepage = "Link to the Shared Module homepage"
                        ios.deploymentTarget = "50.5"
                        framework {
                            baseName = "platform"
                        }
                    }

                    sourceSets {

                        val commonMain by getting {
                            dependencies {
                                api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
                                api("dev.buijs.klutter:annotations-kmp:2022-alpha-1")
                                implementation("io.ktor:ktor-client-core:1.6.7")
                                implementation("io.ktor:ktor-client-auth:1.6.7")
                            }
                        }

                        val commonTest by getting {
                            dependencies {
                                implementation(kotlin("test-common"))
                                implementation(kotlin("test-annotations-common"))
                                implementation(kotlin("test-junit"))
                                implementation("io.ktor:ktor-client-mock:1.6.7")
                                implementation("junit:junit:4.13.2")
                                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
                            }
                        }

                        val androidMain by getting {
                            dependencies {
                                implementation("io.ktor:ktor-client-android:1.6.7")
                                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
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
                                implementation("io.ktor:ktor-client-ios:1.6.7")
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
                    compileSdk = 40
                    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
                    defaultConfig {
                        minSdk = 40
                        targetSdk = 40
                    }
                }
            """.trimIndent())
        }

        "return mid/target/compileSdk" {
            val scanner = PlatformBuildGradleScanner(buildGradle)
            val androidConfig = scanner.androidConfig()
            "compileSdk = ${androidConfig.compileSdk}" shouldBe "compileSdk = 40"
            "targetSdk = ${androidConfig.targetSdk}" shouldBe "targetSdk = 40"
            "minSdk = ${androidConfig.minSdk}" shouldBe "minSdk = 40"

            scanner.iosVersion() shouldBe "50.5"

        }

    }
})