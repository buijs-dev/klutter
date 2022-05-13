plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization") version "1.6.10"
    id("maven-publish")
}

val user = extra.has("repo.username").let {
    if (it) extra.get("repo.username") as String else {
        throw GradleException("missing repo.username in gradle.properties")
    }
}

val pass = extra.has("repo.password").let {
    if (it) extra.get("repo.password") as String else {
        throw GradleException("missing repo.password in gradle.properties")
    }
}

val endpoint = extra.has("repo.url").let {
    if (it) extra.get("repo.url") as String else {
        throw GradleException("missing repo.url in gradle.properties")
    }
}

val libversion = extra.has("annotations.version").let {
    if (it) extra.get("annotations.version") as String else {
        throw GradleException("missing annotations.version in gradle.properties")
    }
}

group = "dev.buijs.klutter"
version = libversion

kotlin {

    android {
        publishLibraryVariants("release", "debug")
    }

    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Klutter module for annotations"
        homepage = "https://buijs.dev"
        ios.deploymentTarget = "13"
        framework {
            baseName = "Annotations"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
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

        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
            }
        }

        val jvmTest by getting
        val androidMain by getting
        val androidAndroidTestRelease by getting

        val androidTest by getting {
            dependsOn(androidAndroidTestRelease)
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
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
                username = user
                password = pass
            }

            url = uri(endpoint)
        }
    }
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {

    outputDirectory.set(buildDir.resolve("dokka"))

    dokkaSourceSets {
        register("annotationsKmp4Jvm") {
            displayName.set("JVM")
            platform.set(org.jetbrains.dokka.Platform.jvm)
            sourceRoots.from(kotlin.sourceSets.getByName("jvmMain").kotlin.srcDirs)
            sourceRoots.from(kotlin.sourceSets.getByName("commonMain").kotlin.srcDirs)
        }

        register("annotationsKmp4Android") {
            displayName.set("Android")
            platform.set(org.jetbrains.dokka.Platform.jvm)
            sourceRoots.from(kotlin.sourceSets.getByName("androidMain").kotlin.srcDirs)
            sourceRoots.from(kotlin.sourceSets.getByName("commonMain").kotlin.srcDirs)
        }

        register("annotationsKmp4Ios") {
            displayName.set("IOS")
            platform.set(org.jetbrains.dokka.Platform.jvm)
            sourceRoots.from(kotlin.sourceSets.getByName("iosMain").kotlin.srcDirs)
            sourceRoots.from(kotlin.sourceSets.getByName("commonMain").kotlin.srcDirs)
        }
    }
}