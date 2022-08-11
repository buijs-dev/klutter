@file:Suppress("UNUSED_VARIABLE")
plugins {
    kotlin("plugin.serialization") version "1.7.0"
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("maven-publish")
    id("klutter")
}

group = "dev.buijs.klutter"
version = dev.buijs.klutter.ProjectVersions.kompose

kotlin {

    android {
        publishLibraryVariants("release", "debug")
    }

    jvm()
    iosX64()
    iosArm64()
    iosArm32()
    iosSimulatorArm64()

    cocoapods {
        summary = "Klutter UI module"
        homepage = "https://buijs.dev"
        ios.deploymentTarget = "14.1"
        framework {
            baseName = "Kompose"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
                implementation(project(":lib:klutter-annotations"))
            }
        }

        val androidMain by getting
        val iosArm32Main by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm32Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }

        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
                implementation("io.github.microutils:kotlin-logging-jvm:2.1.23")
                //implementation("org.jetbrains.kotlin:kotlin-compiler:1.7.10")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
                implementation(project(":lib:klutter-kore"))
                implementation(project(":lib:klutter-annotations"))
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(project(":lib-test"))
            }
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
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {

    outputDirectory.set(buildDir.resolve("dokka"))

    dokkaSourceSets {
        register("kompose") {
            displayName.set("Kompose")
            platform.set(org.jetbrains.dokka.Platform.jvm)
            sourceRoots.from(kotlin.sourceSets.getByName("jvmMain").kotlin.srcDirs)
            sourceRoots.from(kotlin.sourceSets.getByName("commonMain").kotlin.srcDirs)
        }

    }
}

tasks.named<Test>("jvmTest") {
    useJUnitPlatform()
}