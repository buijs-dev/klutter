plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization") version "1.6.10"
    id("maven-publish")
}

group = "dev.buijs.klutter"
version = "0.8.0"//not used!!!

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
    val file = File("${rootDir.absolutePath}/../publish/_publish.properties").normalize()

    if(!file.exists()) {
        throw GradleException("missing _publish.properties file in ${file.absolutePath}")
    }

    val properties = HashMap<String, String>()

    file.forEachLine {
        val pair = it.split("=")
        if(pair.size == 2){
            properties[pair[0]] = pair[1]
        }
    }

    val user = properties["repo.username"]
        ?:throw GradleException("missing repo.username in _publish.properties")

    val pass = properties["repo.password"]
        ?:throw GradleException("missing repo.password in _publish.properties")

    val endpoint = properties["repo.url"]
        ?:throw GradleException("missing repo.url in _publish.properties")

    repositories {
        maven {
            credentials {
                username = user
                password = pass
            }

            url = uri(endpoint)
        }
    }

    publications {
        create<MavenPublication>("maven") {

            version = properties["annotations.version"]
                ?:throw GradleException("annotations.version in _publish.properties")

        }
    }

}