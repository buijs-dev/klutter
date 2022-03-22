plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization") version Versions.kotlin
}

group = App.applicationId
version = App.versionCode

kotlin {
    android()
    iosX64()
    iosArm64()

    cocoapods {
        summary = "Platform module for Klutter App"
        homepage = "N.A."
        ios.deploymentTarget = Ios.version
        framework {
            baseName = "platform"
        }
    }

    sourceSets {

        val commonMain by getting {
            dependencies {
                api(Libraries.klutterAnnotationsKmp)
                api(Libraries.kotlinxSerializationJson)
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
                implementation("junit:junit:${Versions.junit}")
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
    compileSdk = Android.compileSdk.toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = Android.minSdk.toInt()
        targetSdk = Android.targetSdk.toInt()
    }
}

tasks.named("build") {
    doLast {

        //Copy the fat .aar file to the android folder
        copy {
            from(layout.projectDirectory.dir("build/outputs/aar"))
            into(layout.projectDirectory.dir("../android/.klutter/"))
        }

        //Run updatePlatformPodspec to post process the generated podspec file.
        exec {
            workingDir("../")
            commandLine("bash", "./gradlew", "updatePlatformPodspec")
        }

    }
}