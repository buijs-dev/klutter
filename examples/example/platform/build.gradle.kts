apply(from = "${project.projectDir}/../klutter.gradle")

val applicationId: String by extra
val appVersionName: String by extra
val kotlinxVersion: String by extra
val klutterVersion: String by extra
val junitVersion: String by extra
val androidCompileSdk: Int by extra
val androidMinSdk: Int by extra
val androidTargetSdk: Int by extra
val iosVersion: String by extra


plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization") version "1.6.10"
}

group = applicationId
version = appVersionName

kotlin {
    android()
    iosX64()
    iosArm64()

    cocoapods {
        summary = "Platform module for Klutter App"
        homepage = "N.A."
        ios.deploymentTarget = iosVersion
        framework {
            baseName = "platform"
        }
    }

    sourceSets {

        val commonMain by getting {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxVersion")
                api("dev.buijs.klutter:annotations-kmp:$klutterVersion")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val androidMain by getting
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:$junitVersion")
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
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
    compileSdk = androidCompileSdk
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = androidMinSdk
        targetSdk = androidTargetSdk
    }
}

tasks.named("build") {
    doLast {

        //Run updatePlatformPodspec to post process the generated podspec file.
        exec {
            workingDir("../")
            commandLine("bash", "./gradlew", "updatePlatformPodspec")
        }

    }
}