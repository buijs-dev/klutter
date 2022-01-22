apply(from = ".klutter/klutter.gradle.kts")

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization") version "1.6.10"
}

group = "dev.buijs.klutter.example.basic"
version = "1.0.0"

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

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("dev.buijs.klutter:annotations-kmp:$klutterAnnotationsKmpVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxVersion")
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
                implementation("junit:junit:4.13.2")
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
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 31
    }
}