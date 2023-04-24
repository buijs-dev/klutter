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
        summary = "Klutter Kompose module"
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
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation(project(":lib:annotations"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
            }
        }
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
                implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
                implementation(project(":lib:kore"))
                implementation(project(":lib:annotations"))
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

    publications.withType<MavenPublication> {
        artifactId = if (name == "kotlinMultiplatform") {
            "kompose"
        } else {
            "kompose-$name"
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