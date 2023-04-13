@file:Suppress("UNUSED_VARIABLE")
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    kotlin("plugin.serialization") version "1.7.10"
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("maven-publish")
    id("klutter")
}

group = "dev.buijs.klutter"
version = dev.buijs.klutter.ProjectVersions.flutterEngine

kotlin {

    android {
        publishLibraryVariants("release", "debug")
    }

    val xcfName = "FlutterEngine"
    val xcFramework = XCFramework(xcfName)

    ios {
        binaries.framework {
            embedBitcode(Framework.BitcodeEmbeddingMode.DISABLE)
            baseName = xcfName
            xcFramework.add(this)
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            embedBitcode(Framework.BitcodeEmbeddingMode.DISABLE)
            baseName = xcfName
            xcFramework.add(this)
        }
    }

    cocoapods {
        summary = "Klutter - Flutter Engine"
        homepage = "https://buijs.dev"
        ios.deploymentTarget = "13"

        framework {
            baseName = "FlutterEngine"
        }

        pod("flutter_framework") {
            moduleName = "Flutter"
            source = path(project.file("../flutter-engine/lib"))
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
                implementation(project(":lib:kompose"))
                implementation(project(":lib:annotations"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }


        val androidMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
                compileOnly(files("lib/android-arm64/flutter.jar"))
            }
        }
        val androidAndroidTestRelease by getting

        val androidTest by getting {
            dependsOn(androidAndroidTestRelease)
        }

//        val iosArm32Main by getting
//        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        //val iosMain by getting
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

publishing {
    repositories {
        maven {
            credentials {
                username = dev.buijs.klutter.Repository.username
                password = dev.buijs.klutter.Repository.password
            }

            url = dev.buijs.klutter.Repository.endpoint
        }
    }

    publications.withType<MavenPublication> {
        artifactId = if (name == "kotlinMultiplatform") {
            "flutter-engine"
        } else {
            "flutter-engine-$name"
        }
    }

}

tasks.build.get().setFinalizedBy(listOf(tasks.getByName("assembleFlutterEngineXCFramework")))